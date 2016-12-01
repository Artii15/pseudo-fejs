package na.przypale.fitter.repositories.cassandra

import java.util
import java.util.{Calendar, Date, UUID}

import com.datastax.driver.core.utils.UUIDs
import com.datastax.driver.core.{Row, Session, SimpleStatement}
import na.przypale.fitter.entities.Event
import na.przypale.fitter.repositories.exceptions.EventParticipantLimitExceedException
import na.przypale.fitter.repositories.{Dates, EventsRepository}

import scala.collection.JavaConverters

class CassandraEventsRepository(session: Session) extends EventsRepository {

  private lazy val createEventStatement = session.prepare(
    "INSERT INTO events(year, start_date, end_date, id, description, author, name, max_users_count) " +
      "VALUES(:year, :startDate, :endDate, :id, :description, :author, :name, :maxUsersCount)")
  override def create(event: Event): Unit = {
    val eventStartCalendar = Calendar.getInstance()
    eventStartCalendar.setTime(event.startDate)

    val createEventQuery = createEventStatement.bind()
      .setInt("year", eventStartCalendar.get(Calendar.YEAR))
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

  override def findIncoming(): Stream[Event] = {
    val currentYear = Dates.currentYear()
    val eventsYearsToSearch = findEventsYears().filter(year => year >= currentYear)

    val query = findIncomingStatement.bind()
      .setList("years", JavaConverters.seqAsJavaList(eventsYearsToSearch.toSeq))
      .setTimestamp("minimalDate", new Date())

    makeEventsStream(session.execute(query).iterator())
  }

  private def makeEventsStream(iterator: util.Iterator[Row]): Stream[Event] =
    if(iterator.hasNext)
      rowToEvent(iterator.next()) #:: makeEventsStream(iterator)
    else
      Stream.empty

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
    forceUserAssignmentToEvent(event, user)
    val oldestJoinTime = findOldestJoinTimeOfUserToEvent(event, user)
    dropRedundantAssignments(event, user, oldestJoinTime)

    if(!belongsToEvent(event, user))
      throw new EventParticipantLimitExceedException
  }

  private def forceUserAssignmentToEvent(event: Event, user: String): Unit = {
    val joinTime = UUIDs.timeBased()
    assignToParticipants(event.id, user, joinTime)
    assignJoinTime(event.id, user, joinTime)
  }

  private lazy val assignToParticipantsStatement = session.prepare(
    "INSERT INTO events_participants(event_id, participant, join_time) VALUES(:eventId, :participant, :joinTime")
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

  private lazy val selectParticipantJoinTimeStatement = session.prepare(
    "SELECT join_time " +
      "FROM events_participants " +
      "WHERE event_id = :eventId AND participant = :participant " +
      "ORDER BY join_time")
  private def findOldestJoinTimeOfUserToEvent(event: Event, user: String): Option[UUID] = {
    val selectParticipantQuery = selectParticipantJoinTimeStatement.bind()
      .setUUID("eventId", event.id)
      .setString("participant", user)
    selectParticipantQuery.setFetchSize(1)

    val oldestAssignmentRow = session.execute(selectParticipantQuery).one()
    if (oldestAssignmentRow == null) None else Some(oldestAssignmentRow.getUUID("join_time"))
  }

  private lazy val dropRedundantAssignmentsStatement = session.prepare(
    "DELETE FROM events_participants " +
      "WHERE event_id = :eventId AND participant = :participant AND join_time > :oldestJoinTime")
  private def dropRedundantAssignments(event: Event, user: String, oldestJoinTime: Option[UUID]): Unit = {
    if(oldestJoinTime.isDefined) {
      val Some(joinTime) = oldestJoinTime
      val query = dropRedundantAssignmentsStatement.bind()
        .setUUID("eventId", event.id)
        .setString("participant", user)
        .setUUID("oldestJoinTime", joinTime)
      session.execute(query)
    }
  }

  private lazy val selectRelevantParticipantsStatement = session.prepare(
    "SELECT participant FROM events_participants WHERE event_id = :eventId ORDER BY join_time LIMIT :limit")
  private def belongsToEvent(event: Event, user: String): Boolean = {
    val query = selectRelevantParticipantsStatement.bind()
      .setUUID("eventId", event.id)
      .setInt("limit", event.maxParticipantsCount)
    query.setFetchSize(Integer.MAX_VALUE)

    val relevantParticipants = JavaConverters.collectionAsScalaIterable(session.execute(query).all())
    relevantParticipants.exists(row => row.getString("participant") == user)
  }

  private lazy val incrementParticipantsCountStatement = session.prepare(
    "UPDATE events_counters SET current_users_count = current_users_count + 1 WHERE event_id = :eventId")
  private def incrementParticipantsCount(event: Event): Unit = {
    val query = incrementParticipantsCountStatement.bind()
      .setUUID("eventId", event.id)
    session.execute(query)
  }
}
