package fitter.interactions

import fitter.controls.EventsBrowserControls
import fitter.Config
import fitter.entities.Event
import fitter.menu.{Action, ActionIntId}
import fitter.presenters.console.EventPresenter
import fitter.repositories.EventsRepository

import scala.annotation.tailrec

class BrowsingEvents(eventsRepository: EventsRepository, joiningEvent: JoiningEvent) {

  private val controls = new EventsBrowserControls()

  def browse(browsingUserNick: String): Unit = {
    val incomingEvents = eventsRepository.findAllIncoming()
    showEventsToUser(incomingEvents.grouped(Config.DEFAULT_PAGE_SIZE), browsingUserNick)
  }

  @tailrec
  private def showEventsToUser(pagesIterator: Iterator[Stream[Event]], userNick: String): Unit = {
    if(pagesIterator.hasNext) {
      val enumeratedEvents = pagesIterator.next() zip (Stream from 1)
      enumeratedEvents.foreach(display)

      val Action(ActionIntId(selectedActionId), _) = controls.interact()
      selectedActionId match {
        case EventsBrowserControls.MORE_ACTION_ID => showEventsToUser(pagesIterator, userNick)
        case EventsBrowserControls.JOIN_ACTION_ID => joiningEvent.join(userNick, enumeratedEvents)
        case _ =>
      }
    }
    else println("No events")
  }

  private def display(enumeratedEvent: (Event, Int)): Unit = {
    val (event, index) = enumeratedEvent
    println(s"Nr: $index")
    EventPresenter.display(event)
  }
}
