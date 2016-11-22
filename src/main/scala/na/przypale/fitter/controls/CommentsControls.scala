package na.przypale.fitter.controls
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}

class CommentsControls extends Controls{
  override protected def getMenu: Menu = CommentsControls.menu

  override protected def handle(action: Action): Unit = Unit

  override protected def closesControls(action: Action): Boolean = true
}

object CommentsControls {
  val MORE_COMMENTS_ACTION_ID = 1
  val CREATE_COMMENT_ACTION_ID = 2
  val EXIT_ACTION_ID = 3

  val menu = Menu(List(
    Action(ActionIntId(CommentsControls.MORE_COMMENTS_ACTION_ID), s"$MORE_COMMENTS_ACTION_ID - More"),
    Action(ActionIntId(CommentsControls.CREATE_COMMENT_ACTION_ID), s"$CREATE_COMMENT_ACTION_ID - Add comment"),
    Action(ActionIntId(CommentsControls.EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))
}