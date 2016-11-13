package na.przypale.fitter.controls

import na.przypale.fitter.entities.User
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}
import na.przypale.fitter.repositories.UsersRepository

import scala.annotation.tailrec

class LoggedUserControls(val usersRepository: UsersRepository) {
  private val DELETE_ACTION_ID = 1
  private val LOGOUT_ACTION_ID = 2

  val menu = Menu(List(
    Action(ActionIntId(DELETE_ACTION_ID), s"$DELETE_ACTION_ID - Delete account"),
    Action(ActionIntId(LOGOUT_ACTION_ID), s"$LOGOUT_ACTION_ID - Logout")
  ))

  @tailrec
  final def interact(user: User): Unit = {
    menu.display()
    menu.read match {
      case Some(action) => action.id match {
        case ActionIntId(DELETE_ACTION_ID) =>
        case ActionIntId(LOGOUT_ACTION_ID) =>
      }
      case _ => interact(user)
    }
  }
}

object LoggedUserControls {
  def apply(usersRepository: UsersRepository): LoggedUserControls = new LoggedUserControls(usersRepository)
}
