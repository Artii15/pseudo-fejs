package na.przypale.fitter.repositories.cassandra

import java.util.{Calendar, UUID}

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Event
import na.przypale.fitter.repositories.EventsRepository
import na.przypale.fitter.repositories.exceptions.AlreadyParticipatesException

class CassandraEventsRepository(session: Session) extends EventsRepository {

  override def create(event: Event): Unit = {
    insertEvent(event)
    insertEventCounter(event)
  }

  private lazy val createEventStatement = session.prepare(
    "INSERT INTO events(year, start_date, end_date, id, description, author, name, max_users_count) " +
    "VALUES(:year, :startDate, :endDate, :id, :description, :author, :name, :maxUsersCount)")
  private def insertEvent(event: Event): Unit = {
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

  private lazy val createEventCounterStatement = session.prepare(
    "UPDATE events_counters SET current_users_count = 0 WHERE event_id = :eventId")
  private def insertEventCounter(event: Event): Unit = {
    val createEventCounterQuery = createEventCounterStatement.bind()
      .setUUID("eventId", event.id)

    session.execute(createEventCounterQuery)
  }
}
