package na.przypale.fitter.interactions

import na.przypale.fitter.controls.OwnEventsControls
import na.przypale.fitter.entities.Event
import na.przypale.fitter.menu.{Action, ActionIntId}
import na.przypale.fitter.presenters.console.EventPresenter
import na.przypale.fitter.repositories.EventsRepository

import scala.annotation.tailrec

class ShowingUserEvents(eventsRepository: EventsRepository) {

  private val controls = new OwnEventsControls()

  def show(loggedUserNick: String): Unit = {
    val userEvents = eventsRepository.findUserIncomingEvents(loggedUserNick)
    letUserBrowseHisEvents(userEvents, loggedUserNick)
  }

  @tailrec
  private def letUserBrowseHisEvents(events: Stream[Event], user: String): Unit = events match {
    case Stream.empty => println("No more events to show")
    case event #:: moreEvents =>
      EventPresenter.display(event)
      val Action(ActionIntId(userActionId), _) = controls.interact()
      if(userActionId == OwnEventsControls.LEAVE_ACTION_ID) eventsRepository.leave(event, user)
      if(userActionId != OwnEventsControls.EXIT_ACTION_ID) letUserBrowseHisEvents(moreEvents, user)
  }
}
