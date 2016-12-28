package na.przypale.fitter.testers.actors.supervisors

import java.util.UUID

import akka.actor.{Actor, Props}
import na.przypale.fitter.Dependencies
import na.przypale.fitter.testers.actors.bots.AccountCreator
import na.przypale.fitter.testers.commands._
import na.przypale.fitter.testers.config.RegistrationTesterConfig

class RegistrationTester(config: RegistrationTesterConfig, dependencies: Dependencies) extends Actor {

  private var numberOfReceivedStatusesReports = 0
  private var numberOfCreatedAccounts = 0

  override def preStart(): Unit = {
    generateNicks().take(config.numberOfProcesses).foreach(nick => {
      val accountCreator = context.actorOf(Props(classOf[AccountCreator], dependencies))
      accountCreator ! AccountCreateCommand(nick)
    })
  }

  private def generateNicks(): Stream[String] = {
    val nicks = Range.inclusive(1, config.numberOfUniqueNicks).map(_ => UUID.randomUUID().toString)
    Stream.continually(nicks).flatten
  }

  override def receive: Receive = {
    case Start => checkRegistrationResults()
    case AccountCreatingStatus(_, wasAccountCreated) => receiveCreationStatus(wasAccountCreated)
  }

  private def checkRegistrationResults(): Unit = {
    numberOfReceivedStatusesReports = 0
    numberOfCreatedAccounts = 0
    context.children.foreach(_ ! AccountCreatingStatusRequest)
  }

  private def receiveCreationStatus(wasCreated: Boolean): Unit = {
    numberOfReceivedStatusesReports += 1
    if(wasCreated) numberOfCreatedAccounts += 1
    if(numberOfReceivedStatusesReports == config.numberOfProcesses) {
      println(s"Number of created accounts: $numberOfCreatedAccounts")
      println(s"Number of unique nicks: ${config.numberOfUniqueNicks}")
      context.parent ! End
    }
  }
}
