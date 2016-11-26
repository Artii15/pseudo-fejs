package na.przypale.fitter.interactions

import na.przypale.fitter.{CommandLineReader, Config}
import na.przypale.fitter.entities.Event
import na.przypale.fitter.repositories.EventsRepository

import scala.annotation.tailrec

class BrowsingEvents(eventsRepository: EventsRepository) {
  def browse(): Unit = {
    val incomingEvents = eventsRepository.findIncoming()
    val paginatedEvents = incomingEvents.grouped(Config.DEFAULT_PAGE_SIZE).takeWhile(eventsPage => {
      eventsPage.foreach(event => display(event))
      userWantsToSeeNext()
    })

    paginatedEvents.toStream.force
  }

  @tailrec
  private def userWantsToSeeNext(): Boolean = {
    println("1 - more\n2 - exit")
    CommandLineReader.readInt() match {
      case 1 => true
      case 2 => false
      case _ => userWantsToSeeNext()
    }
  }

  private def display(event: Event): Unit = {
    val Event(id, startDate, endDate, maxParticipantsCount, name, description, author) = event
    println(id)
    println(name)
    println(author)
    println(s"Duration: $startDate - $endDate")
    println(s"Max number of participants: $maxParticipantsCount")
    println(maxParticipantsCount)
    println(s"$description\n")
  }
}
