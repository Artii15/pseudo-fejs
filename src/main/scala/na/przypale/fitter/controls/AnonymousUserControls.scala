package na.przypale.fitter.controls

import na.przypale.fitter.entities.User
import na.przypale.fitter.interactions.{CreatingUser, LoggingIn}
import na.przypale.fitter.menu.{Action, ActionIntId, Menu}

class AnonymousUserControls(creatingUser: CreatingUser,
                            loggingIn: LoggingIn,
                            loggedUserControlsFactory: (User => LoggedUserControls))
  extends Controls {

  private val CREATE_USER_ACTION_ID = 1
  private val LOGIN_ACTION_ID = 2
  private val EXIT_ACTION_ID = 3

  private val menu = Menu(List(
    Action(ActionIntId(CREATE_USER_ACTION_ID), s"$CREATE_USER_ACTION_ID - Create user"),
    Action(ActionIntId(LOGIN_ACTION_ID), s"$LOGIN_ACTION_ID - Login"),
    Action(ActionIntId(EXIT_ACTION_ID), s"$EXIT_ACTION_ID - Exit")
  ))

  override protected def getMenu: Menu = menu

  override protected def handle(action: Action): Unit = action.id match {
    case ActionIntId(CREATE_USER_ACTION_ID) => creatingUser.create()
    case ActionIntId(LOGIN_ACTION_ID) => startLoggedUserSession()
    case ActionIntId(EXIT_ACTION_ID) =>
  }

  private def startLoggedUserSession(): Unit = loggingIn.logIn() match {
    case Some(user) => loggedUserControlsFactory(user).interact()
    case _ =>
  }

  override protected def closesControls(action: Action): Boolean = action.id match {
    case ActionIntId(EXIT_ACTION_ID) => true
    case _ => false
  }
}
