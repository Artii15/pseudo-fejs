package na.przypale.fitter.testers.actors.bots

import java.util.UUID

import akka.actor.Actor
import na.przypale.fitter.Dependencies
import na.przypale.fitter.entities.Credentials
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException
import na.przypale.fitter.testers.commands.registration.{AccountCreateCommand, AccountCreatingStatus}

class AccountCreator(dependencies: Dependencies) extends Actor {

  override def receive: Receive = {
    case AccountCreateCommand(nick) => createAccount(nick)
  }

  private def createAccount(nick: String): Unit = {
    try {
      dependencies.creatingUser.create(Credentials(nick, UUID.randomUUID().toString))
      context.parent ! AccountCreatingStatus(true)
    }
    catch {
      case _: UserAlreadyExistsException => context.parent ! AccountCreatingStatus(false)
    }
  }
}
