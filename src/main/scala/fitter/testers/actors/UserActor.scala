package fitter.testers.actors

import akka.actor.{Actor, PoisonPill, Props}
import fitter.testers.commands._
import fitter.CommandLineReader
import fitter.testers.actors.leaders.local.{DeploysMaker, EventsLocalLeader, PostsLocalLeader, RegistrationLocalLeader}
import fitter.testers.commands.events.StartEvent
import fitter.testers.commands.posts.StartPostLikes
import fitter.testers.commands.registration.StartRegistration
import fitter.testers.config.{SessionConfig, SystemConfig}
import fitter.testers.reporters.Reporter
import fitter.testers.results.TaskReport
import fitter.testers.results.events.ParticipantsReport
import fitter.testers.results.registration.RegistrationReport

import scala.annotation.tailrec
import scala.io.StdIn

class UserActor(systemConfig: SystemConfig, sessionConfig: SessionConfig) extends Actor {

  private val deploys = DeploysMaker.make(systemConfig.nodesAddresses, systemConfig.actorSystemName, systemConfig.nodesPort)
  private val numberOfNodes = systemConfig.nodesAddresses.size

  override def receive: Receive = {
    case Start => context.become(listeningForTasksFinishingOnly); interact()
  }

  @tailrec
  private def interact(): Unit = {
    println("1 - Registration tests")
    println("2 - Events joining tests")
    println("3 - Post liking tests")
    println("4 - Exit")

    StdIn.readLine() match {
      case "1" => runRegistrationTests()
      case "2" => runEventsJoiningTests()
      case "3" => runPostLikingTests()
      case "4" => context.system.terminate()
      case _ => interact()
    }
  }

  private def runRegistrationTests(): Unit = {
    print("Number of unique nicks: ")
    val numberOfUniqueNicks = CommandLineReader.readPositiveInt()
    print("Number of threads on each node: ")
    val numberOfThreadsOnEachNode = CommandLineReader.readPositiveInt()

    val registrationLeader = Props(classOf[RegistrationLocalLeader], deploys, sessionConfig)
    context.actorOf(registrationLeader) ! new StartRegistration(numberOfNodes, numberOfUniqueNicks, numberOfThreadsOnEachNode)
  }

  private def runEventsJoiningTests(): Unit = {
    print("Max number of event participants: ")
    val numberOfParticipants = CommandLineReader.readPositiveInt()
    print("Event author: ")
    val author = CommandLineReader.readString()
    print("Number of threads on each node: ")
    val numberOfThreadsOnEachNode = CommandLineReader.readPositiveInt()

    val eventsLeader = Props(classOf[EventsLocalLeader], deploys, sessionConfig)
    context.actorOf(eventsLeader) ! new StartEvent(numberOfNodes, numberOfThreadsOnEachNode, author, numberOfParticipants)
  }

  private def runPostLikingTests(): Unit = {
    print("Number of unique users: ")
    val numberOfUniqueUsers = CommandLineReader.readPositiveInt()
    print("Post author: ")
    val author = CommandLineReader.readString()
    print("Number of threads on each node: ")
    val numberOfThreadsOnEachNode = CommandLineReader.readPositiveInt()

    val postsLeader = Props(classOf[PostsLocalLeader], deploys, sessionConfig)
    context.actorOf(postsLeader) ! new StartPostLikes(numberOfNodes, numberOfThreadsOnEachNode, author, numberOfUniqueUsers)
  }

  private def listeningForTasksFinishingOnly: Receive = {
    case taskReport: TaskReport => finishTask(taskReport)
  }

  private def finishTask(taskReport: TaskReport): Unit = {
    Reporter.showReport(taskReport)
    sender() ! PoisonPill
    interact()
  }
}
