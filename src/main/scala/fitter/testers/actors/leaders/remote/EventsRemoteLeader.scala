package fitter.testers.actors.leaders.remote

import akka.actor.Props
import fitter.entities.Event
import fitter.testers.actors.leaders.SessionOwner
import fitter.testers.actors.workers.EventWorker
import fitter.testers.commands.events.{JoinEvent, MakeParticipants}
import fitter.testers.commands.nodes.TaskStart
import fitter.testers.config.SessionConfig
import fitter.testers.results.AggregatedResults
import fitter.testers.results.events.{JoinedParticipant, JoinedParticipants}

class EventsRemoteLeader(sessionConfig: SessionConfig)
  extends SessionOwner[MakeParticipants, JoinedParticipant](sessionConfig) {

  private var eventToJoin: Option[Event] = None

  override protected val results: AggregatedResults[JoinedParticipant] = new JoinedParticipants()

  override protected def readTask(task: MakeParticipants): Unit = eventToJoin = Some(task.event)

  override protected def makeWorker(workerId: Int): Props =
    Props(classOf[EventWorker], dependencies.cassandraEventsRepository, dependencies.creatingUser)

  override protected def makeTaskForWorker(workerId: Int): TaskStart = JoinEvent(eventToJoin.get)
}
