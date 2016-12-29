package na.przypale.fitter.testers.actors.bots

import java.util.UUID

import akka.actor.Actor
import na.przypale.fitter.Dependencies
import na.przypale.fitter.entities.Credentials
import na.przypale.fitter.repositories.exceptions.{UserAlreadyExistsException, UserNotExistsException}
import na.przypale.fitter.testers.commands.registration.{AccountCreateCommand, AccountCreatingStatus}

class AccountCreator(dependencies: Dependencies) extends Actor {

  override def receive: Receive = {
    case AccountCreateCommand(nick) => createAccount(nick)
  }

  private def createAccount(nick: String): Unit = {
    val credentials = Credentials(nick, UUID.randomUUID().toString)
    try {
      dependencies.creatingUser.create(credentials)
      context.parent ! AccountCreatingStatus(wasAccountCreated = true, credentials)
    }
    catch {
      case _: UserAlreadyExistsException | _: UserNotExistsException =>
        context.parent ! AccountCreatingStatus(wasAccountCreated = false, credentials)
    }
  }
}
