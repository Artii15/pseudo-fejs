package na.przypale.fitter.repositories.cassandra

import java.util.{Date, UUID}

import com.datastax.driver.core.utils.UUIDs
import com.datastax.driver.core.{Row, Session, SimpleStatement}
import na.przypale.fitter.Dates
import na.przypale.fitter.entities.Event
import na.przypale.fitter.repositories.exceptions.{EventParticipantAlreadyAssigned, EventParticipantLimitExceedException}
import na.przypale.fitter.repositories.EventsRepository

import scala.collection.JavaConverters

class CassandraEventsRepository(session: Session) extends EventsRepository {

  private lazy val createEventStatement = session.prepare(
    "INSERT INTO events(year, start_date, end_date, id, description, author, name, max_users_count) " +
      "VALUES(:year, :startDate, :endDate, :id, :description, :author, :name, :maxUsersCount)")
  override def create(event: Event): Unit = {

    val createEventQuery = createEventStatement.bind()
      .setInt("year", Dates.extractYear(event.startDate))
      .setTimestamp("startDate", event.startDate)
      .setTimestamp("endDate", event.endDate)
      .setUUID("id", event.id)
      .setString("description", event.description)
      .setString("author", event.author)
      .setString("name", event.name)
      .setInt("maxUsersCount", event.maxParticipantsCount)
    session.execute(createEventQuery)
  }

  private lazy val findIncomingStatement = session.prepare(
    "SELECT year, start_date, end_date, id, description, author, name, max_users_count " +
    "FROM events " +
    "WHERE year IN :years AND start_date > :minimalDate")

  override def findAllIncoming(): Stream[Event] = {
    val currentYear = Dates.currentYear()
    val eventsYearsToSearch = findEventsYears().filter(year => year >= currentYear)

    val query = findIncomingStatement.bind()
      .setList("years", JavaConverters.seqAsJavaList(eventsYearsToSearch.toSeq))
      .setTimestamp("minimalDate", new Date())

    JavaConverters.asScalaIterator(session.execute(query).iterator()).toStream.map(rowToEvent)
  }

  private def rowToEvent(row: Row) = Event(
    row.getUUID("id"),
    row.getTimestamp("start_date"),
    row.getTimestamp("end_date"),
    row.getInt("max_users_count"),
    row.getString("name"),
    row.getString("description"),
    row.getString("author")
  )

  private lazy val findEventsYearsStatement = new SimpleStatement("SELECT year FROM events")
  def findEventsYears(): Iterable[Int] = JavaConverters
    .collectionAsScalaIterable(session.execute(findEventsYearsStatement).all())
    .map(row => row.getInt("year"))

  override def assignUserToEvent(event: Event, user: String): Unit = {
    if(hasTriedToAssign(event, user)) throw new EventParticipantAlreadyAssigned
    forceUserAssignmentToEvent(event, user)
    if(!belongsToEvent(event, user)) throw new EventParticipantLimitExceedException
  }

  private lazy val checkUserAssignmentStatement = session.prepare(
    "SELECT * FROM users_events " +
    "WHERE nick = :nick AND year = :year AND start_date = :startDate AND end_date = :endDate AND event_id = :eventId")
  private def hasTriedToAssign(event: Event, user: String): Boolean = {
    val query = checkUserAssignmentStatement.bind()
      .setString("nick", user)
      .setInt("year", Dates.extractYear(event.startDate))
      .setTimestamp("startDate", event.startDate)
      .setTimestamp("endDate", event.endDate)
      .setUUID("eventId", event.id)
    session.execute(query).one() != null
  }

  private def forceUserAssignmentToEvent(event: Event, user: String): Unit = {
    val joinTime = UUIDs.timeBased()
    assignToParticipants(event.id, user, joinTime)
    assignJoinTime(event.id, user, joinTime)
    logAssignmentAttempt(event, user)
  }

  private lazy val assignToParticipantsStatement = session.prepare(
    "INSERT INTO events_participants(event_id, participant, join_time) VALUES(:eventId, :participant, :joinTime)")
  private def assignToParticipants(eventId: UUID, participant: String, joinTime: UUID): Unit = {
    val assignToParticipantsQuery = assignToParticipantsStatement.bind()
      .setUUID("eventId", eventId)
      .setString("participant", participant)
      .setUUID("joinTime", joinTime)
    session.execute(assignToParticipantsQuery)
  }

  private lazy val assignJoinTimeStatement = session.prepare(
    "INSERT INTO events_join_times(event_id, join_time, participant) VALUES(:eventId, :joinTime, :participant)")
  private def assignJoinTime(eventId: UUID, participant: String, joinTime: UUID): Unit = {
    val assignJoinTimeQuery = assignJoinTimeStatement.bind()
      .setUUID("eventId", eventId)
      .setUUID("joinTime", joinTime)
      .setString("participant", participant)
    session.execute(assignJoinTimeQuery)
  }

  private lazy val logAssignmentAttemptStatement = session.prepare(
    "INSERT INTO users_events(nick, year, start_date, end_date, event_id) " +
    "VALUES(:nick, :year, :startDate, :endDate, :eventId)")
  private def logAssignmentAttempt(event: Event, user: String): Unit = {
    val query = logAssignmentAttemptStatement.bind()
      .setString("nick", user)
      .setInt("year", Dates.extractYear(event.startDate))
      .setTimestamp("startDate", event.startDate)
      .setTimestamp("endDate", event.endDate)
      .setUUID("eventId", event.id)
    session.execute(query)
  }

  private lazy val selectRelevantParticipantsStatement = session.prepare(
    "SELECT participant FROM events_join_times WHERE event_id = :eventId ORDER BY join_time")
  private def belongsToEvent(event: Event, user: String): Boolean = {
    val query = selectRelevantParticipantsStatement.bind()
      .setUUID("eventId", event.id)
    query.setFetchSize(event.maxParticipantsCount)

    JavaConverters.asScalaIterator(session.execute(query).iterator()).toStream
      .map(row => row.getString("participant"))
      .distinct
      .take(event.maxParticipantsCount)
      .contains(user)
  }

  private lazy val findUserIncomingEventsStatement = session.prepare(
    "SELECT event_id, start_date FROM users_events WHERE nick = :nick AND year = :year ORDER BY start_date")
  override def findUserIncomingEvents(user: String): Stream[Event] = {
    val currentYear = Dates.currentYear()
    val query = findUserIncomingEventsStatement.bind()
      .setString("nick", user)
      .setInt("year", currentYear)

    JavaConverters.asScalaIterator(session.execute(query).iterator()).toStream
      .map(row => findEvent(currentYear, row.getTimestamp("start_date"), row.getUUID("event_id")))
      .filter(_.isDefined)
      .map(_.get)
      .filter(event => belongsToEvent(event, user))
  }

  private lazy val findSingleEventStatement = session.prepare(
    "SELECT * FROM events WHERE year = :year AND start_date = :startDate AND id = :id")
  private def findEvent(year: Int, startDate: Date, eventId: UUID): Option[Event] = {
    val query = findSingleEventStatement.bind()
      .setInt("year", year)
      .setTimestamp("startDate", startDate)
      .setUUID("id", eventId)

    session.execute(query).one() match {
      case null => None
      case row => Some(rowToEvent(row))
    }
  }
}