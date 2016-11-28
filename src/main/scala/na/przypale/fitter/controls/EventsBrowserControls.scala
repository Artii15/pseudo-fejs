package na.przypale.fitter.controls
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}

class EventsBrowserControls extends PassiveControls {
  override protected def getMenu: Menu = EventsBrowserControls.menu
}

object EventsBrowserControls {
  val MORE_ACTION_ID = 1
  val EXIT_ACTION_ID = 2

  private lazy val menu = new Menu(List(
    Action(ActionIntId(MORE_ACTION_ID), s"$MORE_ACTION_ID - More"),
    Action(ActionIntId(EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))
}
