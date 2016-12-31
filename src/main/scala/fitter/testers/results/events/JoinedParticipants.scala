package fitter.testers.results.events

import fitter.entities.Credentials
import fitter.testers.results.AggregatedResults

import scala.collection.mutable.ListBuffer

class JoinedParticipants extends AggregatedResults[JoinedParticipant] {

  val participants: ListBuffer[Credentials] = ListBuffer.empty

  override def combine(joinedParticipant: JoinedParticipant): Unit = {
    val JoinedParticipant(participant) = joinedParticipant
    if(participant.isDefined) participants += participant.get
  }

  override def clear(): Unit = participants.clear()
}
