package na.przypale.fitter.controls

import na.przypale.fitter.entities.User
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}
import na.przypale.fitter.repositories.UsersRepository

class LoggedUserControls(val user: User, val usersRepository: UsersRepository) extends Controls {
  private val DELETE_ACTION_ID = 1
  private val CREATE_POST_ACTION_ID = 2
  private val LOGOUT_ACTION_ID = 3

  val menu = Menu(List(
    Action(ActionIntId(DELETE_ACTION_ID), s"$DELETE_ACTION_ID - Delete account"),
    Action(ActionIntId(DELETE_ACTION_ID), s"$CREATE_POST_ACTION_ID - Create post"),
    Action(ActionIntId(LOGOUT_ACTION_ID), s"$LOGOUT_ACTION_ID - Logout")
  ))

  override protected def getMenu: Menu = menu

  override protected def handle(action: Action): Unit = action.id match {
    case ActionIntId(DELETE_ACTION_ID) => usersRepository.delete(user)
    case ActionIntId(CREATE_POST_ACTION_ID) =>
    case ActionIntId(LOGOUT_ACTION_ID) =>
  }

  override protected def closesControls(action: Action): Boolean = true
}

object LoggedUserControls {
  def apply(user: User, usersRepository: UsersRepository): LoggedUserControls =
    new LoggedUserControls(user, usersRepository)

  def makeFactory(usersRepository: UsersRepository) = {
    user: User => LoggedUserControls(user, usersRepository)
  }
}
