package fitter.controls
import fitter.entities.User
import fitter.interactions._
import fitter.menu.{Action, ActionIntId, Menu}

class EventsControls(loggedUser: User,
                     creatingEvent: CreatingEvent,
                     browsingEvents: BrowsingEvents,
                     showingUserEvents: ShowingUserEvents) extends Controls {
  override protected def getMenu: Menu = EventsControls.menu

  override protected def handle(action: Action): Unit = action.id match {
    case ActionIntId(EventsControls.CREATE_ACTION_ID) => creatingEvent.create(loggedUser.nick)
    case ActionIntId(EventsControls.BROWSE_ACTION_ID) => browsingEvents.browse(loggedUser.nick)
    case ActionIntId(EventsControls.SHOW_USER_INCOMING_ACTION_ID) => showingUserEvents.show(loggedUser.nick)
    case _ =>
  }

  override protected def closesControls(action: Action): Boolean = action.id match {
    case ActionIntId(EventsControls.EXIT_ACTION_ID) => true
    case _ => false
  }
}

object EventsControls {
  private val CREATE_ACTION_ID = 1
  private val BROWSE_ACTION_ID = 2
  private val SHOW_USER_INCOMING_ACTION_ID = 3
  private val EXIT_ACTION_ID = 4

  private val menu = Menu(List(
    Action(ActionIntId(CREATE_ACTION_ID), s"$CREATE_ACTION_ID - Create event"),
    Action(ActionIntId(BROWSE_ACTION_ID), s"$BROWSE_ACTION_ID - Browse events"),
    Action(ActionIntId(SHOW_USER_INCOMING_ACTION_ID), s"$SHOW_USER_INCOMING_ACTION_ID - Show your incoming events"),
    Action(ActionIntId(EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))

  def factory(creatingEvent: CreatingEvent, browsingEvents: BrowsingEvents,
              showingUserEvents: ShowingUserEvents): (User => EventsControls) = {
    user => new EventsControls(user, creatingEvent, browsingEvents, showingUserEvents)
  }
}
