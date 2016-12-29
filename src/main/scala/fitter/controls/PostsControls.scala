package fitter.controls
import fitter.menu.{Action, ActionIntId, Menu}

class PostsControls extends PassiveControls {
  override protected def getMenu: Menu = PostsControls.menu
}

object PostsControls {
  val MORE_POSTS_ACTION_ID = 1
  val DISPLAY_POST_ACTION_ID = 2
  val EXIT_ACTION_ID = 3

  val menu = Menu(List(
    Action(ActionIntId(MORE_POSTS_ACTION_ID), s"$MORE_POSTS_ACTION_ID - More"),
    Action(ActionIntId(DISPLAY_POST_ACTION_ID), s"$DISPLAY_POST_ACTION_ID - Display post"),
    Action(ActionIntId(EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))
}
