package na.przypale.fitter.testers.actors.supervisors

import akka.actor.{Actor, Props}
import na.przypale.fitter.entities.Credentials
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
  private var registeredAccounts: Stream[Iterable[Credentials]] = Stream.empty

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

    val nicks = RandomStringsGenerator.generateRandomStrings(numberOfUniqueNicks)
    val testerPropsGenerator = (dependencies: Dependencies) => Props(classOf[RegistrationTester], dependencies)
    context.children.foreach(agent => {
      agent ! Deployment(testerPropsGenerator)
      agent ! AccountsCreatingCommand(numberOfThreadsOnNode, nicks)
    })
    context.become(waitingForRegistrationToFinish)
    workingNodes = numberOfNodes
    registeredAccounts = Stream.empty
  }

  private def waitingForRegistrationToFinish: Receive = {
    case AccountsCreatingTaskEnd(createdAccounts) => collectRegistrationStatus(createdAccounts)
  }

  private def collectRegistrationStatus(createdAccounts: Iterable[Credentials]): Unit = {
    workingNodes -= 1
    registeredAccounts = createdAccounts #:: registeredAccounts
    if(workingNodes == 0) {
      val registeredAccountsFlatList = registeredAccounts.flatten
      println("Registered accounts credentials:")
      registeredAccountsFlatList.foreach { case Credentials(nick, password) => println(s"$nick\t$password") }
      println(s"Number of registered accounts: ${registeredAccountsFlatList.size}")
      context.become(receive)
      self ! Start
    }
  }
}
