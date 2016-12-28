package na.przypale.fitter.testers.actors.supervisors

import akka.actor.{Actor, Props}
import na.przypale.fitter.{CommandLineReader, Dependencies}
import na.przypale.fitter.testers.actors.DeployGenerator
import na.przypale.fitter.testers.commands._
import na.przypale.fitter.testers.commands.nodes.Deployment
import na.przypale.fitter.testers.config.{RegistrationTesterConfig, UserActorConfig}

import scala.annotation.tailrec
import scala.io.StdIn

class UserActor(config: UserActorConfig) extends Actor {

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

    val testerConfig = new RegistrationTesterConfig(numberOfThreadsOnNode, numberOfUniqueNicks)
    val testerPropsGenerator = (dependencies: Dependencies) => Props(classOf[RegistrationTester], testerConfig, dependencies)
    context.children.foreach(agent => {
      agent ! Deployment(UserActor.registrationDeploymentID, testerPropsGenerator)
      agent ! TaskStart(UserActor.registrationDeploymentID)
    })
    context.become(waitingForTaskToFinish)
  }

  private def waitingForTaskToFinish: Receive = {
    case TaskEnd(results) => finishTask(results)
  }

  private def finishTask(results: String): Unit = {
    println(results)
    context.become(receive)
    self ! Start
  }
}

object UserActor {
  private val registrationDeploymentID = "REGISTRATION"
}
