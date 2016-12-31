package fitter.controls
import fitter.menu.{Action, ActionIntId, Menu}

class EventsBrowserControls extends PassiveControls {
  override protected def getMenu: Menu = EventsBrowserControls.menu
}

object EventsBrowserControls {
  val MORE_ACTION_ID = 1
  val JOIN_ACTION_ID = 2
  val EXIT_ACTION_ID = 3

  private lazy val menu = new Menu(List(
    Action(ActionIntId(MORE_ACTION_ID), s"$MORE_ACTION_ID - More"),
    Action(ActionIntId(JOIN_ACTION_ID), s"$JOIN_ACTION_ID - Join"),
    Action(ActionIntId(EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))
}
