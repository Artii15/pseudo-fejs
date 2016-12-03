package na.przypale.fitter.interactions

import na.przypale.fitter.controls.EventsBrowserControls
import na.przypale.fitter.Config
import na.przypale.fitter.entities.Event
import na.przypale.fitter.menu.{Action, ActionIntId}
import na.przypale.fitter.presenters.console.EventPresenter
import na.przypale.fitter.repositories.EventsRepository

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
