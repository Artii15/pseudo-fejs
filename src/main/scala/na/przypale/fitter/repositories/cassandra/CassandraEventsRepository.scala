package na.przypale.fitter.repositories.cassandra

import java.util.{Calendar, UUID}

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Event
import na.przypale.fitter.repositories.EventsRepository

import scala.collection.JavaConverters

class CassandraEventsRepository(session: Session) extends EventsRepository {

  override def create(event: Event): Unit = {
    insertEvent(event)
    insertEventCounter(event)
  }

  private lazy val createEventStatement = session.prepare(
    "INSERT INTO events(year, start_date, end_date, id, participants, description, author, name) " +
      "VALUES(:year, :startDate, :endDate, :id, :participants, :description, :author, :name)")
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

    session.execute(createEventQuery)
  }

  private lazy val createEventCounterStatement = session.prepare(
    "UPDATE events_counters SET current_users_count = 0 WHERE id = :id AND max_users_count = :maxUsersCount")
  private def insertEventCounter(event: Event): Unit = {
    val createEventCounterQuery = createEventCounterStatement.bind()
      .setUUID("id", event.id)
      .setInt("maxUsersCount", event.maxParticipantsCount)

    session.execute(createEventCounterQuery)
  }

  private lazy val joinEventStatement = session.prepare(
    "SELECT id, max_users_count, current_users_count FROM events_counters WHERE id = :id AND ")
  override def join(eventId: UUID): Unit = {

  }
}
