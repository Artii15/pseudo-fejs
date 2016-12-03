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
    "INSERT INTO users_events(nick, year, start_date, end_date, id, description, author, name, max_users_count) " +
    "VALUES(:nick, :year, :startDate, :endDate, :id, :description, :author, :name: maxUsersCount)")
  private def logAssignmentAttempt(event: Event, user: String): Unit = {
    val query = logAssignmentAttemptStatement.bind()
      .setString("nick", user)
      .setInt("year", Dates.extractYear(event.startDate))
      .setTimestamp("startDate", event.startDate)
      .setTimestamp("endDate", event.endDate)
      .setUUID("id", event.id)
      .setString("description", event.description)
      .setString("author", event.author)
      .setString("name", event.name)
      .setInt("maxUsersCount", event.maxParticipantsCount)
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
    "SELECT * FROM users_events WHERE nick = :nick AND year = :year ORDER BY start_date")
  override def findUserIncomingEvents(user: String): Stream[Event] = {
    val currentYear = Dates.currentYear()
    val query = findUserIncomingEventsStatement.bind()
      .setString("nick", user)
      .setInt("year", currentYear)

    JavaConverters.asScalaIterator(session.execute(query).iterator()).toStream
      .map(rowToEvent)
      .filter(event => belongsToEvent(event, user))
  }

  override def leave(event: Event, user: String): Unit = {
    val joinTimes = findUserJoinTimes(event, user)
    removeFromEventsJoinTimes(event, user, joinTimes)
    removeFromEventsParticipants(event, user)
    removeFromUsersEvents(event, user)
  }

  private lazy val findUserJoinTimesStatement = session.prepare(
    "SELECT join_time FROM events_participants WHERE event_id = :eventId AND participant = :participant")
  private def findUserJoinTimes(event: Event, user: String): Iterable[UUID] = {
    val query = findUserJoinTimesStatement.bind()
      .setUUID("eventId", event.id)
      .setString("participant", user)

    JavaConverters.collectionAsScalaIterable(session.execute(query).all())
      .map(row => row.getUUID("join_time"))
  }

  private lazy val removeFromEventsJoinTimesStatement = session.prepare(
    "DELETE FROM events_join_times WHERE event_id = :eventId AND participant = :participant AND join_time IN :joinTimes")
  private def removeFromEventsJoinTimes(event: Event, user: String, joinTimes: Iterable[UUID]): Unit = {
    val query = removeFromEventsJoinTimesStatement.bind()
      .setUUID("eventId", event.id)
      .setString("participant", user)
      .setList("joinTimes", JavaConverters.seqAsJavaList(joinTimes.toSeq))
    session.execute(query)
  }

  private lazy val removeFromEventsParticipantsStatement = session.prepare(
    "DELETE FROM events_participants WHERE event_id = :eventId AND participant = :participant")
  private def removeFromEventsParticipants(event: Event, user: String): Unit = {
    val query = removeFromEventsParticipantsStatement.bind()
      .setUUID("eventId", event.id)
      .setString("participant", user)
    session.execute(query)
  }

  private lazy val removeFromUsersEventsStatement = session.prepare(
    "DELETE FROM users_events WHERE nick = :nick AND year = :year AND start_date = :startDate AND id = :id")
  private def removeFromUsersEvents(event: Event, user: String): Unit = {
    val query = removeFromUsersEventsStatement.bind()
      .setString("nick", user)
      .setInt("year", Dates.extractYear(event.startDate))
      .setTimestamp("startDate", event.startDate)
      .setUUID("id", event.id)
    session.execute(query)
  }
}