package fitter.testers.actors.bots.registration

import akka.actor.{Actor, PoisonPill, Props}
import fitter.Dependencies
import fitter.entities.Credentials
import fitter.testers.commands.registration.{AccountCreateCommand, AccountCreatingStatus, AccountsCreatingCommand, AccountsCreatingTaskEnd}

import scala.collection.mutable.ArrayBuffer

class AccountsCreatorsLeader(dependencies: Dependencies) extends Actor {

  private var createdAccounts: ArrayBuffer[Credentials] = ArrayBuffer.empty
  private var numberOfStatusesReportsToReceive = 0

  override def receive: Receive = {
    case command: AccountsCreatingCommand => startRegistering(command)
  }

  private def startRegistering(command: AccountsCreatingCommand): Unit = {
    createdAccounts.clear()
    numberOfStatusesReportsToReceive = command.numberOfProcesses

    Stream.continually(command.nicks).flatten.take(command.numberOfProcesses).foreach(nick => {
      context.actorOf(Props(classOf[AccountCreator], dependencies)) ! AccountCreateCommand(nick)
    })
    context.become(waitingForCreatingStatuses)
  }

  private def waitingForCreatingStatuses: Receive = {
    case creatingStatus: AccountCreatingStatus => receiveCreationStatus(creatingStatus)
  }

  private def receiveCreationStatus(creatingStatus: AccountCreatingStatus): Unit = {
    if (creatingStatus.wasAccountCreated) createdAccounts += creatingStatus.credentials
    numberOfStatusesReportsToReceive -= 1
    if (numberOfStatusesReportsToReceive == 0) {
      context.parent ! AccountsCreatingTaskEnd(createdAccounts)
      context.children.foreach(_ ! PoisonPill)
      context.become(receive)
    }
  }
}
