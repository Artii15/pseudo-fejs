package na.przypale.fitter.controls
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}

class PostControls extends Controls {

  override protected def getMenu: Menu = PostControls.menu

  override protected def closesControls(action: Action): Boolean = true

  override protected def handle(action: Action): Unit = Unit
}

object PostControls {
  val MORE_POSTS_ACTION_ID = 1
  val EXIT_ACTION_ID = 2

  val menu = Menu(List(
    Action(ActionIntId(PostControls.MORE_POSTS_ACTION_ID), s"$MORE_POSTS_ACTION_ID - More"),
    Action(ActionIntId(PostControls.EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))
}
