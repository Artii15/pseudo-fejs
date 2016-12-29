package fitter.controls
import fitter.menu.{Action, ActionIntId, Menu}

class OwnEventsControls extends PassiveControls {
  override protected def getMenu: Menu = new Menu(OwnEventsControls.actions)
}

object OwnEventsControls {
  val NEXT_ACTION_ID = 1
  val LEAVE_ACTION_ID = 2
  val EXIT_ACTION_ID = 3

  private lazy val actions = List(
    Action(ActionIntId(NEXT_ACTION_ID), s"$NEXT_ACTION_ID - Next"),
    Action(ActionIntId(LEAVE_ACTION_ID), s"$LEAVE_ACTION_ID - Leave event"),
    Action(ActionIntId(EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  )
}
