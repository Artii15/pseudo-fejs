package na.przypale.fitter.interactions

import na.przypale.fitter.{CommandLineReader, Config}
import na.przypale.fitter.entities.Event
import na.przypale.fitter.repositories.EventsRepository

import scala.annotation.tailrec

class BrowsingEvents(eventsRepository: EventsRepository) {
  def browse(): Unit = {
    val incomingEvents = eventsRepository.findIncoming()
    showEventsToUser(incomingEvents.grouped(Config.DEFAULT_PAGE_SIZE))
  }

  @tailrec
  private def showEventsToUser(eventsPages: Iterator[Stream[Event]]): Unit = {
    if(eventsPages.hasNext) {
      val eventsPage = eventsPages.next()
      eventsPage.foreach(display)
      if (userWantsToSeeNext()) showEventsToUser(eventsPages)
    }
    else println("No events")
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
