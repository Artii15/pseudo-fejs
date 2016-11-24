package na.przypale.fitter.interactions

import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.Event
import na.przypale.fitter.repositories.EventsRepository

class CreatingEvent(eventsRepository: EventsRepository) {
  def create(creator: String): Unit = {
    print("Event name: ")
    val name = CommandLineReader.readString()

    print("Description: ")
    val description = CommandLineReader.readString()

    val datesFormat = "dd-mm-YYYY"
    print(s"Event begin date ($datesFormat): ")
    val beginDate = CommandLineReader.readDate(datesFormat)

    print(s"Event end date ($datesFormat): ")
    val endDate = CommandLineReader.readDate(datesFormat)

    val event = Event(UUIDs.random(), beginDate, endDate, name, description, creator)
    eventsRepository.create(event)
  }
}
