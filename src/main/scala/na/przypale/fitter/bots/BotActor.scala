package na.przypale.fitter.bots

import java.util.UUID

import akka.actor.Actor
import na.przypale.fitter.bots.commands.Start
import na.przypale.fitter.entities.{Credentials, User}
import na.przypale.fitter.logic.exceptions.AuthenticationException
import na.przypale.fitter.logic.{Authenticating, CreatingUser}
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException

import scala.annotation.tailrec

class BotActor(creatingUser: CreatingUser, authenticating: Authenticating) extends Actor {
  private var loggedUser: Option[User] = None

  override def receive: Receive = {
    case Start => gainAccessToSystem()
  }

  @tailrec
  private def gainAccessToSystem(): Unit = {
    try {
      val registeredUserCredentials = register()
      loggedUser = Some(logIn(registeredUserCredentials))
    }
    catch {
      case _: AuthenticationException => gainAccessToSystem()
    }
  }

  @tailrec
  private def register(): Credentials = {
    val credentials = Credentials(UUID.randomUUID().toString, UUID.randomUUID().toString)
    try {
      creatingUser.create(credentials)
      credentials
    }
    catch {
      case _: UserAlreadyExistsException => register()
    }
  }

  private def logIn(credentials: Credentials): User = authenticating.authenticate(credentials)
}
