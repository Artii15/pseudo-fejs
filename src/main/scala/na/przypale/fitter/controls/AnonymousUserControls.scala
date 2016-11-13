package na.przypale.fitter.controls

import na.przypale.fitter.interactions.{CreatingUser, DeletingUser}
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}
import na.przypale.fitter.repositories.UsersRepository

import scala.annotation.tailrec

class AnonymousUserControls(usersRepository: UsersRepository, loggedUserControls: LoggedUserControls) {
  private val CREATE_USER_ACTION_ID = 1
  private val DELETE_USER_ACTION_ID = 2
  private val LOGIN_ACTION_ID = 3
  private val EXIT_ACTION_ID = 4

  private val menu = Menu(List(
    Action(ActionIntId(CREATE_USER_ACTION_ID), s"$CREATE_USER_ACTION_ID - Create user"),
    Action(ActionIntId(DELETE_USER_ACTION_ID), s"$DELETE_USER_ACTION_ID - Delete user"),
    Action(ActionIntId(LOGIN_ACTION_ID), s"$LOGIN_ACTION_ID - Login"),
    Action(ActionIntId(EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))

  @tailrec
  final def interact(): Unit = {
    menu.display()
    menu.read match {
      case Some(action) => action.id match {
        case ActionIntId(CREATE_USER_ACTION_ID) =>
          CreatingUser.create(usersRepository)
          interact()
        case ActionIntId(DELETE_USER_ACTION_ID) =>
          DeletingUser.delete(usersRepository)
          interact()
        case ActionIntId(LOGIN_ACTION_ID) =>
          loggedUserControls.interact()
          interact()
        case ActionIntId(EXIT_ACTION_ID) =>
      }
      case _ => interact()
    }
  }
}

object AnonymousUserControls {
  def apply(usersRepository: UsersRepository): AnonymousUserControls = new AnonymousUserControls(usersRepository)
}
