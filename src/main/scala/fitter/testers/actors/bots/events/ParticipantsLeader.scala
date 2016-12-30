package fitter.testers.actors.bots.events

import akka.actor.{Actor, Props}
import fitter.Dependencies
import fitter.testers.commands.events.{JoinEvent, JoiningEventTaskEnd, JoiningStatus, RunParticipants}
import fitter.testers.generators.RandomStringsGenerator

import scala.collection.mutable.ArrayBuffer

class ParticipantsLeader(dependencies: Dependencies) extends Actor {

  private val joinedParticipants: ArrayBuffer[String] = ArrayBuffer.empty
  private var numberOfStatusesReportsToReceive = 0

  override def receive: Receive = {
    case command: RunParticipants => runParticipants(command)
  }

  private def runParticipants(command: RunParticipants): Unit = {
    joinedParticipants.clear()
    numberOfStatusesReportsToReceive = command.numberOfProcesses

    Stream.from(0).take(command.numberOfProcesses).foreach(_ => {
      val nick = RandomStringsGenerator.generateRandomString()
      context.actorOf(Props(classOf[EventParticipant], dependencies)) ! JoinEvent(nick, command.event)
    })
    context.become(waitingForParticipants)
  }

  private def waitingForParticipants: Receive = {
    case joiningStatus: JoiningStatus => receiveJoiningStatus(joiningStatus)
  }

  private def receiveJoiningStatus(joiningStatus: JoiningStatus): Unit = {
    if (joiningStatus.joined) joinedParticipants += joiningStatus.participant
    numberOfStatusesReportsToReceive -= 1
    if (numberOfStatusesReportsToReceive == 0) {
      context.parent ! JoiningEventTaskEnd(joinedParticipants)
      context.become(receive)
    }
  }
}
