package na.przypale.fitter.testers.actors.supervisors

import java.util.UUID

import akka.actor.{Actor, Props}
import na.przypale.fitter.Dependencies
import na.przypale.fitter.testers.actors.bots.AccountCreator
import na.przypale.fitter.testers.commands.nodes.{TaskEnd, TaskStart}
import na.przypale.fitter.testers.commands.registration.{AccountCreateCommand, AccountCreatingStatus}
import na.przypale.fitter.testers.config.RegistrationTesterConfig

class RegistrationTester(config: RegistrationTesterConfig, dependencies: Dependencies) extends Actor {

  private var numberOfReceivedStatusesReports = 0
  private var numberOfCreatedAccounts = 0

  override def receive: Receive = {
    case _: TaskStart => startRegistering()
  }

  private def startRegistering(): Unit = {
    numberOfCreatedAccounts = 0
    numberOfReceivedStatusesReports = 0

    generateNicks().take(config.numberOfProcesses).foreach(nick => {
      context.actorOf(Props(classOf[AccountCreator], dependencies)) ! AccountCreateCommand(nick)
    })
    context.become(waitingForCreatingStatuses)
  }

  private def generateNicks(): Stream[String] = {
    val nicks = Range.inclusive(1, config.numberOfUniqueNicks).map(_ => UUID.randomUUID().toString)
    Stream.continually(nicks).flatten
  }

  private def waitingForCreatingStatuses: Receive = {
    case AccountCreatingStatus(wasAccountCreated) => receiveCreationStatus(wasAccountCreated)
  }

  private def receiveCreationStatus(wasAccountCreated: Boolean): Unit = {
    if (wasAccountCreated) numberOfCreatedAccounts += 1
    numberOfReceivedStatusesReports += 1
    if (numberOfReceivedStatusesReports == config.numberOfProcesses) {
      context.parent ! TaskEnd(printReport())
      context.become(receive)
    }
  }

  private def printReport(): String = s"Number of created accounts $numberOfCreatedAccounts"
}
