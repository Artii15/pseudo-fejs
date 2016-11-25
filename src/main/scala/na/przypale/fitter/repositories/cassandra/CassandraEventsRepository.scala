package na.przypale.fitter.repositories.cassandra

import java.util.{Calendar, UUID}

import com.datastax.driver.core.Session
import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.entities.{Event, EventParticipation, EventParticipationRequest}
import na.przypale.fitter.repositories.EventsRepository
import na.przypale.fitter.repositories.exceptions.AlreadyParticipatesException

import scala.collection.JavaConverters

class CassandraEventsRepository(session: Session) extends EventsRepository {

  override def create(event: Event): Unit = {
    insertEvent(event)
    insertEventCounter(event)
  }

  private lazy val createEventStatement = session.prepare(
    "INSERT INTO events(year, start_date, end_date, id, participants, description, author, name, max_users_count) " +
      "VALUES(:year, :startDate, :endDate, :id, :participants, :description, :author, :name, :maxUsersCount)")
  private def insertEvent(event: Event): Unit = {
    val eventStartCalendar = Calendar.getInstance()
    eventStartCalendar.setTime(event.startDate)

    val createEventQuery = createEventStatement.bind()
      .setInt("year", eventStartCalendar.get(Calendar.YEAR))
      .setTimestamp("startDate", event.startDate)
      .setTimestamp("endDate", event.endDate)
      .setUUID("id", event.id)
      .setSet("participants", JavaConverters.setAsJavaSet(event.participants))
      .setString("description", event.description)
      .setString("author", event.author)
      .setString("name", event.name)
      .setInt("maxUsersCount", event.maxParticipantsCount)

    session.execute(createEventQuery)
  }

  private lazy val createEventCounterStatement = session.prepare(
    "UPDATE events_counters SET current_users_count = 0 WHERE event_id = :eventId")
  private def insertEventCounter(event: Event): Unit = {
    val createEventCounterQuery = createEventCounterStatement.bind()
      .setUUID("eventId", event.id)

    session.execute(createEventCounterQuery)
  }

  override def join(eventParticipation: EventParticipationRequest): EventParticipation = {
    findParticipantJoinTime(eventParticipation) match {
      case None => addParticipation(eventParticipation)
      case _ => throw new AlreadyParticipatesException
    }
  }

  private lazy val findParticipantJoinTimeStatement = session.prepare(
    "SELECT join_time FROM events_participants WHERE event_id = :eventId AND participant = :participant")
  private def findParticipantJoinTime(eventParticipation: EventParticipationRequest): Option[UUID] = {
    val EventParticipationRequest(participant, eventId) = eventParticipation
    val query = findParticipantJoinTimeStatement.bind()
      .setUUID("eventId", eventId)
      .setString("participant", participant)

    session.execute(query).one() match {
      case null => None
      case row => Some(row.getUUID("join_time"))
    }
  }

  private lazy val addToParticipantsListStatement = session.prepare(
    "INSERT INTO events_participants(event_id, participant, join_time)" +
      "VALUES(:eventId, :participant, :joinTime")
  private def addParticipation(eventParticipationRequest: EventParticipationRequest): EventParticipation = {
    val applicationTime = UUIDs.timeBased()
    val EventParticipationRequest(participant, eventId) = eventParticipationRequest

    val query = addToParticipantsListStatement.bind()
      .setUUID("eventId", eventId)
      .setString("participant", participant)
      .setUUID("joinTime", applicationTime)

    session.execute(query)
    EventParticipation(eventId, participant, applicationTime)
  }
}
