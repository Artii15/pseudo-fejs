package na.przypale.fitter.controls
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}

class UserContentControls extends PassiveControls{
  override protected def getMenu: Menu = UserContentControls.menu
}

object UserContentControls {
  val MORE_COMMENTS_ACTION_ID = 1
  val LIKE_ACTION_ID = 2
  val CREATE_COMMENT_ACTION_ID = 3
  val DISPLAY_COMMENT_ACTION_ID = 4
  val EXIT_ACTION_ID = 5

  val menu = Menu(List(
    Action(ActionIntId(UserContentControls.MORE_COMMENTS_ACTION_ID), s"$MORE_COMMENTS_ACTION_ID - More"),
    Action(ActionIntId(UserContentControls.LIKE_ACTION_ID), s"$LIKE_ACTION_ID - Like!"),
    Action(ActionIntId(UserContentControls.CREATE_COMMENT_ACTION_ID), s"$CREATE_COMMENT_ACTION_ID - Add comment"),
    Action(ActionIntId(UserContentControls.DISPLAY_COMMENT_ACTION_ID), s"$DISPLAY_COMMENT_ACTION_ID - Display comment"),
    Action(ActionIntId(UserContentControls.EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))
}