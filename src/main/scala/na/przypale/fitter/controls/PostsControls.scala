package na.przypale.fitter.controls
import na.przypale.fitter.entities.Post
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}

class PostsControls(posts: Iterable[Post]) extends Controls {

  private val EXIT_ACTION_ID = 1
  private val MORE_ACTION_ID = 2
  private val READ_ACTION_ID = 3

  val exitAction = Action(ActionIntId(EXIT_ACTION_ID), "Exit")
  val menuActions = if(posts.isEmpty) List(exitAction) else List(
    exitAction,
    Action(ActionIntId(MORE_ACTION_ID), "More"),
    Action(ActionIntId(READ_ACTION_ID), "Read post")
  )

  val menu = new Menu(menuActions)

  override protected def getMenu: Menu = menu

  override protected def handle(action: Action): Unit = action.id match {
    case ActionIntId(MORE_ACTION_ID) =>
    case _ =>
  }

  override protected def closesControls(action: Action): Boolean = action.id match {
    case ActionIntId(id) => id == EXIT_ACTION_ID
    case _ => false
  }
}
