package na.przypale.fitter.testers.actors.supervisors

import akka.actor.{Actor, AddressFromURIString, Deploy, Props}
import akka.remote.RemoteScope
import na.przypale.fitter.testers.commands._
import na.przypale.fitter.testers.config.{SessionConfig, UserActorConfig}

import scala.annotation.tailrec
import scala.io.StdIn

class UserActor(config: UserActorConfig) extends Actor {

  override def preStart(): Unit = {
    val systemConfig = config.systemConfig
    systemConfig.nodesAddresses.foreach(address => {
      val actorNodeAddress = s"akka.tcp://${systemConfig.actorSystemName}@$address:${systemConfig.nodesPort}"
      val deploy = new Deploy(RemoteScope(AddressFromURIString(actorNodeAddress)))

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
