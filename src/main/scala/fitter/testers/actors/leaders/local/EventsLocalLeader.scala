package fitter.testers.actors.leaders.local

import akka.actor.{Deploy, Props}
import fitter.entities.Event
import fitter.testers.actors.leaders.SessionOwner
import fitter.testers.actors.leaders.remote.EventsRemoteLeader
import fitter.testers.commands.TaskStart
import fitter.testers.commands.events.{MakeParticipants, StartEvent}
import fitter.testers.config.SessionConfig
import fitter.testers.generators.EventsGenerator
import fitter.testers.results.AggregatedResults
import fitter.testers.results.events.{JoinedParticipants, ParticipantsReport}

class EventsLocalLeader(deploys: Iterator[Deploy], sessionConfig: SessionConfig)
  extends SessionOwner[StartEvent, JoinedParticipants](sessionConfig) {

  private var event: Option[Event] = None
  private var numberOfWorkersPerNode = 0
  override protected val results: AggregatedResults[JoinedParticipants] = new ParticipantsReport()

  override protected def readTask(task: StartEvent): Unit = {
    event = Some(EventsGenerator.generateNextYearEvent(task.eventAuthor, task.maxNumberOfEventParticipants))
    dependencies.cassandraEventsRepository.createConsistently(event.get)
    numberOfWorkersPerNode = task.numberOfWorkersPerNode
  }

  override protected def makeWorker(workerId: Int): Props =
    Props(classOf[EventsRemoteLeader], sessionConfig).withDeploy(deploys.next())

  override protected def makeTaskForWorker(workerId: Int): TaskStart =
    new MakeParticipants(numberOfWorkersPerNode, event.get)
}
