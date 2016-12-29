package na.przypale.fitter.testers.actors.supervisors

import akka.actor.{Actor, Props}
import na.przypale.fitter.Dependencies
import na.przypale.fitter.testers.actors.RandomStringsGenerator
import na.przypale.fitter.testers.actors.bots.AccountCreator
import na.przypale.fitter.testers.commands.registration.{AccountCreateCommand, AccountCreatingStatus, AccountsCreatingCommand, AccountsCreatingTaskEnd}

class RegistrationTester(dependencies: Dependencies) extends Actor {

  private var numberOfCreatedAccounts = 0
  private var numberOfStatusesReportsToReceive = 0

  override def receive: Receive = {
    case command: AccountsCreatingCommand => startRegistering(command)
  }

  private def startRegistering(command: AccountsCreatingCommand): Unit = {
    numberOfCreatedAccounts = 0
    numberOfStatusesReportsToReceive = command.numberOfProcesses

    Stream.continually(command.nicks).flatten.take(command.numberOfProcesses).foreach(nick => {
      context.actorOf(Props(classOf[AccountCreator], dependencies)) ! AccountCreateCommand(nick)
    })
    context.become(waitingForCreatingStatuses)
  }

  private def waitingForCreatingStatuses: Receive = {
    case AccountCreatingStatus(wasAccountCreated) => receiveCreationStatus(wasAccountCreated)
  }

  private def receiveCreationStatus(wasAccountCreated: Boolean): Unit = {
    if (wasAccountCreated) numberOfCreatedAccounts += 1
    numberOfStatusesReportsToReceive -= 1
    if (numberOfStatusesReportsToReceive == 0) {
      context.parent ! AccountsCreatingTaskEnd(numberOfCreatedAccounts)
      context.become(receive)
    }
  }
}
