package fitter.testers.actors.leaders.local

import akka.actor.Props
import fitter.entities.Event
import fitter.testers.actors.leaders.SessionOwner
import fitter.testers.commands.events.StartEvent
import fitter.testers.commands.nodes.TaskStart
import fitter.testers.config.SessionConfig
import fitter.testers.generators.EventsGenerator
import fitter.testers.results.AggregatedResults
import fitter.testers.results.events.{JoinedParticipants, ParticipantsReport}

class EventsLocalLeader(sessionConfig: SessionConfig)
  extends SessionOwner[StartEvent, JoinedParticipants](sessionConfig) {

  private var event: Option[Event] = None
  override protected val results: AggregatedResults[JoinedParticipants] = new ParticipantsReport()

  override protected def readTask(task: StartEvent): Unit = {
    event = Some(EventsGenerator.generateNextYearEvent(task.eventAuthor, task.maxNumberOfEventParticipants))
    dependencies.cassandraEventsRepository.createConsistently(event.get)
  }

  override protected def makeWorker(workerId: Int): Props = ???

  override protected def makeTaskForWorker(workerId: Int): TaskStart = ???
}
