package fitter.testers.actors.supervisors

import akka.actor.{Actor, Props}
import fitter.testers.commands._
import fitter.testers.config.{SessionConfig, SystemConfig}

import scala.annotation.tailrec
import scala.io.StdIn

class UserActor(systemConfig: SystemConfig, sessionConfig: SessionConfig) extends Actor {

  override def receive: Receive = {
    case Start => interact()
  }

  @tailrec
  private def interact(): Unit = {
    println("1 - Registration tests")
    println("2 - Events joining tests")
    println("3 - Exit")

    StdIn.readLine() match {
      case "1" => runRegistrationTests()
      //case "2" => runEventsJoiningTests()
      case "3" => context.system.terminate()
      case _ => interact()
    }
  }

  private def runRegistrationTests(): Unit = {
    val registrationSupervisor = context.actorOf(Props(classOf[RegistrationSupervisor], systemConfig, sessionConfig))
  }
}
