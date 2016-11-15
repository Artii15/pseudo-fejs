package na.przypale.fitter.controls

import na.przypale.fitter.entities.User
import na.przypale.fitter.interactions.CreatingPost
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}

class LoggedUserControls(val user: User,
                         val creatingPost: CreatingPost) extends Controls {
  private val DELETE_ACTION_ID = 1
  private val CREATE_POST_ACTION_ID = 2
  private val BROWSE_POSTS_ACTION_ID = 3
  private val SUBSCRIBE_ACTION_ID = 4
  private val LOGOUT_ACTION_ID = 5

  val menu = Menu(List(
    Action(ActionIntId(DELETE_ACTION_ID), s"$DELETE_ACTION_ID - Delete account"),
    Action(ActionIntId(CREATE_POST_ACTION_ID), s"$CREATE_POST_ACTION_ID - Create post"),
    Action(ActionIntId(BROWSE_POSTS_ACTION_ID), s"$BROWSE_POSTS_ACTION_ID - Browse posts"),
    Action(ActionIntId(SUBSCRIBE_ACTION_ID), s"$SUBSCRIBE_ACTION_ID - Subscribe user"),
    Action(ActionIntId(LOGOUT_ACTION_ID), s"$LOGOUT_ACTION_ID - Logout")
  ))

  override protected def getMenu: Menu = menu

  override protected def handle(action: Action): Unit = action.id match {
    case ActionIntId(DELETE_ACTION_ID) => //TODO Create interaction for deleting users
    case ActionIntId(CREATE_POST_ACTION_ID) => creatingPost.create(user)
    case ActionIntId(SUBSCRIBE_ACTION_ID) =>
    case ActionIntId(LOGOUT_ACTION_ID) =>
  }

  override protected def closesControls(action: Action): Boolean = action.id match {
    case ActionIntId(CREATE_POST_ACTION_ID) => false
    case _ => true
  }
}

object LoggedUserControls {
  def apply(user: User, creatingPost: CreatingPost): LoggedUserControls =
    new LoggedUserControls(user, creatingPost)

  def makeFactory(creatingPost: CreatingPost) = {
    user: User => LoggedUserControls(user, creatingPost)
  }
}
