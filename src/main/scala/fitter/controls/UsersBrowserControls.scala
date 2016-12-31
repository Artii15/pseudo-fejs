package fitter.controls
import fitter.menu.{Action, ActionIntId, Menu}

class UsersBrowserControls extends PassiveControls {
  override protected def getMenu: Menu = UsersBrowserControls.menu
}

object UsersBrowserControls {
  val MORE_USERS_ACTION_ID = 1
  val EXIT_ACTION_ID = 2

  val menu = Menu(List(
    Action(ActionIntId(MORE_USERS_ACTION_ID), s"$MORE_USERS_ACTION_ID - More"),
    Action(ActionIntId(EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))
}
