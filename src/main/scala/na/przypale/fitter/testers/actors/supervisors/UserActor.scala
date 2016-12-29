package na.przypale.fitter.testers.actors.supervisors

import akka.actor.{Actor, Props}
import na.przypale.fitter.{CommandLineReader, Dependencies}
import na.przypale.fitter.testers.actors.{DeployGenerator, RandomStringsGenerator}
import na.przypale.fitter.testers.commands._
import na.przypale.fitter.testers.commands.nodes.Deployment
import na.przypale.fitter.testers.commands.registration.{AccountsCreatingCommand, AccountsCreatingTaskEnd}
import na.przypale.fitter.testers.config.UserActorConfig

import scala.annotation.tailrec
import scala.io.StdIn

class UserActor(config: UserActorConfig) extends Actor {

  private val numberOfNodes = config.systemConfig.nodesAddresses.size
  private var workingNodes = 0
  private var numberOfRegisteredAccounts = 0

  override def preStart(): Unit = {
    val systemConfig = config.systemConfig
    systemConfig.nodesAddresses.foreach(address => {
      val deploy = DeployGenerator.makeRemoteDeploy(systemConfig.actorSystemName, address, systemConfig.nodesPort)
      context.actorOf(Props(classOf[BootstrappingAgent], config.sessionConfig).withDeploy(deploy))
    })
  }

  override def receive: Receive = {
    case Start => interact()
  }

  @tailrec
  private def interact(): Unit = {
    println("1 - registration tests")
    println("2 - exit")

    StdIn.readLine() match {
      case "1" => runRegistrationTests()
      case "2" => context.system.terminate()
      case _ => interact()
    }
  }

  private def runRegistrationTests(): Unit = {
    print("Number of threads on each node: ")
    val numberOfThreadsOnNode = CommandLineReader.readPositiveInt()
    print("Number of unique nicks: ")
    val numberOfUniqueNicks = CommandLineReader.readPositiveInt()

    val testerPropsGenerator = (dependencies: Dependencies) => Props(classOf[RegistrationTester], dependencies)
    context.children.foreach(agent => {
      agent ! Deployment(testerPropsGenerator)
      agent ! AccountsCreatingCommand(numberOfThreadsOnNode, RandomStringsGenerator.generateRandomStrings(numberOfUniqueNicks))
    })
    context.become(waitingForRegistrationToFinish)
    workingNodes = numberOfNodes
    numberOfRegisteredAccounts = 0
  }

  private def waitingForRegistrationToFinish: Receive = {
    case AccountsCreatingTaskEnd(numberOfCreatedAccounts) => collectRegistrationStatus(numberOfCreatedAccounts)
  }

  private def collectRegistrationStatus(numberOfCreatedAccounts: Int): Unit = {
    workingNodes -= 1
    numberOfRegisteredAccounts += numberOfCreatedAccounts
    if(workingNodes == 0) {
      println(s"Number of registered accounts: $numberOfRegisteredAccounts")
      context.become(receive)
      self ! Start
    }
  }
}
