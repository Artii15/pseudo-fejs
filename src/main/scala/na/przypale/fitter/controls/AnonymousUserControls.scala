package na.przypale.fitter.controls

import na.przypale.fitter.interactions.{CreatingUser, LoggingIn}
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}
import na.przypale.fitter.repositories.UsersRepository

import scala.annotation.tailrec

class AnonymousUserControls(usersRepository: UsersRepository, loggedUserControls: LoggedUserControls) {
  private val CREATE_USER_ACTION_ID = 1
  private val LOGIN_ACTION_ID = 2
  private val EXIT_ACTION_ID = 3

  private val menu = Menu(List(
    Action(ActionIntId(CREATE_USER_ACTION_ID), s"$CREATE_USER_ACTION_ID - Create user"),
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
        case ActionIntId(LOGIN_ACTION_ID) =>
          startLoggedUserSession()
          interact()
        case ActionIntId(EXIT_ACTION_ID) =>
      }
      case _ => interact()
    }
  }

  private def startLoggedUserSession(): Unit = LoggingIn.logIn(usersRepository) match {
    case None =>
    case Some(user) => loggedUserControls.interact(user)
  }
}

object AnonymousUserControls {
  def apply(usersRepository: UsersRepository, loggedUserControls: LoggedUserControls): AnonymousUserControls =
    new AnonymousUserControls(usersRepository, loggedUserControls)
}
