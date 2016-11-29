package na.przypale.fitter.interactions

import na.przypale.fitter.controls.EventsBrowserControls
import na.przypale.fitter.Config
import na.przypale.fitter.entities.Event
import na.przypale.fitter.menu.{Action, ActionIntId}
import na.przypale.fitter.repositories.EventsRepository

import scala.annotation.tailrec

class BrowsingEvents(eventsRepository: EventsRepository) {

  def browse(): Unit = {
    val incomingEvents = eventsRepository.findIncoming()
    showEventsToUser(incomingEvents.grouped(Config.DEFAULT_PAGE_SIZE))
  }

  private val controls = new EventsBrowserControls()

  @tailrec
  private def showEventsToUser(pagesIterator: Iterator[Stream[Event]]): Unit = {
    if(pagesIterator.hasNext) {
      pagesIterator.next().foreach(display)

      val Action(ActionIntId(selectedActionId), _) = controls.interact()
      if (selectedActionId == EventsBrowserControls.MORE_ACTION_ID)
        showEventsToUser(pagesIterator)
    }
    else println("No events")
  }

  private def display(event: Event): Unit = {
    val Event(id, startDate, endDate, maxParticipantsCount, name, description, author) = event
    println(s"Id: $id")
    println(s"Name: $name")
    println(s"Author: $author")
    println(s"Duration: $startDate - $endDate")
    println(s"Max number of participants: $maxParticipantsCount")
    println(s"$description\n")
  }
}
