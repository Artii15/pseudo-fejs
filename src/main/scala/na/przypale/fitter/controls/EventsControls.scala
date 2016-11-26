package na.przypale.fitter.controls
import na.przypale.fitter.entities.User
import na.przypale.fitter.interactions._
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}

class EventsControls(loggedUser: User,
                     creatingEvent: CreatingEvent,
                     browsingEvents: BrowsingEvents) extends Controls {
  override protected def getMenu: Menu = EventsControls.menu

  override protected def handle(action: Action): Unit = action.id match {
    case ActionIntId(EventsControls.CREATE_ACTION_ID) => creatingEvent.create(loggedUser.nick)
    case ActionIntId(EventsControls.BROWSE_ACTION_ID) => browsingEvents.browse()
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
  private val JOIN_ACTION_ID = 3
  private val LEAVE_ACTION_ID = 4
  private val DELETE_ACTION_ID = 5
  private val EXIT_ACTION_ID = 6

  private val menu = Menu(List(
    Action(ActionIntId(CREATE_ACTION_ID), s"$CREATE_ACTION_ID - Create event"),
    Action(ActionIntId(BROWSE_ACTION_ID), s"$BROWSE_ACTION_ID - Browse events"),
    Action(ActionIntId(JOIN_ACTION_ID), s"$JOIN_ACTION_ID - Join event"),
    Action(ActionIntId(LEAVE_ACTION_ID), s"$LEAVE_ACTION_ID - Leave event"),
    Action(ActionIntId(DELETE_ACTION_ID), s"$DELETE_ACTION_ID - Delete event"),
    Action(ActionIntId(EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))

  def factory(creatingEvent: CreatingEvent, browsingEvents: BrowsingEvents): (User => EventsControls) = {
    user => new EventsControls(user, creatingEvent, browsingEvents)
  }
}
