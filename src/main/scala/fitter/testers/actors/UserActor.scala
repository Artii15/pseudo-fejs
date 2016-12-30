package fitter.testers.actors

import akka.actor.{Actor, PoisonPill, Props}
import fitter.connectors.{ClusterConnector, SessionConnector}
import fitter.testers.commands._
import fitter.testers.config._
import fitter.{CommandLineReader, Dependencies}

import scala.annotation.tailrec
import scala.io.StdIn

class UserActor(config: TestsSupervisorConfig) extends Actor {

  private val cluster = ClusterConnector.makeConnection(config.sessionConfig.contactPoint)
  private val session = SessionConnector.makeSession(cluster, config.sessionConfig.keyspace)
  private val dependencies = new Dependencies(session)

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
      case "2" => runEventsJoiningTests()
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
    val supervisorProps = Props(classOf[RegistrationSupervisor], config, registrationConfig)
    context.actorOf(supervisorProps) ! Start
  }

  private def runEventsJoiningTests(): Unit = {
    print("Number of participants: ")
    val numberOfParticipants = CommandLineReader.readPositiveInt()
    print("Number of threads on each node: ")
    val numberOfThreadsOnEachNode = CommandLineReader.readPositiveInt()

    val eventsJoiningConfig = new EventsJoiningConfig(numberOfThreadsOnEachNode, numberOfParticipants)
    val supervisorProps = Props(classOf[EventsJoiningSupervisor], config, eventsJoiningConfig, dependencies)
    context.actorOf(supervisorProps) ! Start
  }

  private def listeningForTasksFinishingOnly: Receive = {
    case Finish(report) => finishTask(report)
  }

  private def finishTask(report: String): Unit = {
    println(report)
    sender() ! PoisonPill
    interact()
  }
}
