package fitter.testers.actors.supervisors

import akka.actor.{Actor, Props}
import fitter.CommandLineReader
import fitter.testers.commands._
import fitter.testers.config.{RegistrationTestConfig, SessionConfig, SystemConfig}

import scala.annotation.tailrec
import scala.io.StdIn

class UserActor(systemConfig: SystemConfig, sessionConfig: SessionConfig) extends Actor {

  override def receive: Receive = {
    case Start => context.become(listeningForTasksFinishingOnly); interact()
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
    print("Number of unique nicks: ")
    val numberOfUniqueNicks = CommandLineReader.readPositiveInt()
    print("Number of threads on each node: ")
    val numberOfThreadsOnEachNode = CommandLineReader.readPositiveInt()

    val registrationConfig = new RegistrationTestConfig(numberOfUniqueNicks, numberOfThreadsOnEachNode)
    val supervisorProps = Props(classOf[RegistrationSupervisor], systemConfig, sessionConfig, registrationConfig)
    context.actorOf(supervisorProps) ! Start
  }

  private def listeningForTasksFinishingOnly: Receive = {
    case Finish(report) => println(report); interact()
  }
}
