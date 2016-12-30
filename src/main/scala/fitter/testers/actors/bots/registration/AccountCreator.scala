package fitter.testers.actors.bots.registration

import java.util.UUID

import akka.actor.Actor
import fitter.Dependencies
import fitter.entities.Credentials
import fitter.repositories.exceptions.{UserAlreadyExistsException, UserNotExistsException}
import fitter.testers.commands.registrationOld.{AccountCreateCommand, AccountCreatingStatus}

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
