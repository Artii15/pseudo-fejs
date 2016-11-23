package na.przypale.fitter.interactions

import java.util.Date

import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.Event

class CreatingEvent {
  def create(creator: String): Unit = {
    println("Event name: ")
    val name = CommandLineReader.readString()
    println("Description: ")
    val description = CommandLineReader.readString()

    val event = Event(UUIDs.random(), new Date(), new Date(), name, description, creator)
  }
}
