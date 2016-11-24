package na.przypale.fitter.repositories.cassandra

import java.util.Calendar

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Event
import na.przypale.fitter.repositories.EventsRepository

import scala.collection.JavaConverters

class CassandraEventsRepository(session: Session) extends EventsRepository {

  private lazy val createEventStatement = session.prepare(
    "INSERT INTO events(year, start_date, end_date, id, participants, description, author, name) " +
    "VALUES(:year, :startDate, :endDate, :id, :participants, :description, :author, :name)")
  override def create(event: Event): Unit = {
    val eventStartCalendar = Calendar.getInstance()
    eventStartCalendar.setTime(event.startDate)

    val query = createEventStatement.bind()
      .setInt("year", eventStartCalendar.get(Calendar.YEAR))
      .setTimestamp("startDate", event.startDate)
      .setTimestamp("endDate", event.endDate)
      .setUUID("id", event.id)
      .setSet("participants", JavaConverters.setAsJavaSet(event.participants))
      .setString("description", event.description)
      .setString("author", event.author)
      .setString("name", event.name)

    session.execute(query)
  }
}
