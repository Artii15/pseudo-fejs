package fitter.testers.results.events

import fitter.entities.Credentials
import fitter.testers.results.AggregatedResults

import scala.collection.mutable.ListBuffer

class ParticipantsReport extends AggregatedResults[JoinedParticipants] {

  val participants: ListBuffer[Credentials] = ListBuffer.empty

  override def combine(joinedParticipants: JoinedParticipants): Unit = participants ++= joinedParticipants.participants

  override def clear(): Unit = participants.clear()
}
