package na.przypale.fitter.controls

import na.przypale.fitter.entities.User
import na.przypale.fitter.interactions._
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}

class LoggedUserControls(val user: User,
                         val creatingPost: CreatingPost,
                         val deletingUser: DeletingUser,
                         val subscribingUser: SubscribingUser,
                         val browsingPosts: BrowsingPosts,
                         val searchingForUsers: SearchingForUsers) extends Controls {

  private val DELETE_ACTION_ID = 1
  private val FIND_USER_ACTION_ID = 2
  private val CREATE_POST_ACTION_ID = 3
  private val BROWSE_POSTS_ACTION_ID = 4
  private val SUBSCRIBE_ACTION_ID = 5
  private val LOGOUT_ACTION_ID = 6

  private val menu = Menu(List(
    Action(ActionIntId(DELETE_ACTION_ID), s"$DELETE_ACTION_ID - Delete account"),
    Action(ActionIntId(FIND_USER_ACTION_ID), s"$FIND_USER_ACTION_ID - Find user"),
    Action(ActionIntId(CREATE_POST_ACTION_ID), s"$CREATE_POST_ACTION_ID - Create post"),
    Action(ActionIntId(BROWSE_POSTS_ACTION_ID), s"$BROWSE_POSTS_ACTION_ID - Browse posts"),
    Action(ActionIntId(SUBSCRIBE_ACTION_ID), s"$SUBSCRIBE_ACTION_ID - Subscribe user"),
    Action(ActionIntId(LOGOUT_ACTION_ID), s"$LOGOUT_ACTION_ID - Logout")
  ))

  override protected def getMenu: Menu = menu

  override protected def handle(action: Action): Unit = action.id match {
    case ActionIntId(DELETE_ACTION_ID) => deletingUser.delete(user)
    case ActionIntId(FIND_USER_ACTION_ID) => searchingForUsers.search()
    case ActionIntId(CREATE_POST_ACTION_ID) => creatingPost.create(user)
    case ActionIntId(SUBSCRIBE_ACTION_ID) => subscribingUser.createSubscription(user)
    case ActionIntId(BROWSE_POSTS_ACTION_ID) => browsingPosts.browse(user)
    case ActionIntId(LOGOUT_ACTION_ID) =>
  }

  private val actionsFinishingUserSession = Set(DELETE_ACTION_ID, LOGOUT_ACTION_ID)
  override protected def closesControls(action: Action): Boolean = action.id match {
    case ActionIntId(actionId) => actionsFinishingUserSession.contains(actionId)
    case _ => false
  }
}

object LoggedUserControls {
  def makeFactory(creatingPost: CreatingPost, deletingUser: DeletingUser, subscribingUser: SubscribingUser,
                  browsingPosts: BrowsingPosts, searchingForUsers: SearchingForUsers) = {
    user: User => new LoggedUserControls(user, creatingPost, deletingUser, subscribingUser, browsingPosts, searchingForUsers)
  }
}
