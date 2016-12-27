package na.przypale.fitter.testers.actors.bots

import java.util.UUID

import akka.actor.Actor
import na.przypale.fitter.Dependencies
import na.przypale.fitter.entities.Credentials
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException
import na.przypale.fitter.testers.commands.{AccountCreateCommand, AccountCreatingStatus, AccountCreatingStatusRequest}

class AccountCreator(dependencies: Dependencies) extends Actor {
  private var requestedNick: Option[String] = None
  private var accountCreated: Option[Boolean] = None

  override def receive: Receive = {
    case AccountCreateCommand(nick) => createAccount(nick)
    case AccountCreatingStatusRequest => reportCreatingStatus()
  }

  private def createAccount(nick: String): Unit = {
    requestedNick = Some(nick)
    try {
      dependencies.creatingUser.create(Credentials(nick, UUID.randomUUID().toString))
      accountCreated = Some(true)
    }
    catch {
      case _: UserAlreadyExistsException => accountCreated = Some(false)
    }
  }

  private def reportCreatingStatus(): Unit = {
    assert(requestedNick.isDefined)
    assert(accountCreated.isDefined)
    sender() ! AccountCreatingStatus(requestedNick.get, accountCreated.get)
  }
}
