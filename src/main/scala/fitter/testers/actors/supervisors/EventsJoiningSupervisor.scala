package fitter.testers.actors.supervisors

import akka.actor.Props
import fitter.Dependencies
import fitter.testers.actors.bots.events.ParticipantsLeader
import fitter.testers.commands.events.{JoiningEventTaskEnd, RunParticipants}
import fitter.testers.commands.nodes.{Deployment, TaskEnd, TaskStart}
import fitter.testers.config.EventsJoiningConfig
import fitter.testers.generators.EventsGenerator

import scala.collection.mutable.ArrayBuffer

class EventsJoiningSupervisor(testsSupervisorConfig: TestsSupervisorConfig,
                              eventsJoiningConfig: EventsJoiningConfig,
                              dependencies: Dependencies)
  extends TestsSupervisor(testsSupervisorConfig) {

  private val deployment = Deployment((localDependencies: Dependencies) => Props(classOf[ParticipantsLeader], localDependencies))
  private val event = EventsGenerator.generateNextYearEvent("supervisor", eventsJoiningConfig.numberOfParticipants)
  private val joinedParticipants: ArrayBuffer[Iterable[String]] = ArrayBuffer.empty

  override def preStart(): Unit = {
    dependencies.eventsRepository.create(event)
    super.preStart()
  }

  override protected def generateDeployment(): Deployment = deployment

  override protected def generateTaskStart(): TaskStart = RunParticipants(event, eventsJoiningConfig.numberOfParticipants)

  override protected def onTaskEndOnSingleNode(taskEnd: TaskEnd): Unit = {
    val joiningTaskEnd = taskEnd.asInstanceOf[JoiningEventTaskEnd]
    joinedParticipants += joiningTaskEnd.participants
  }

  override protected def onTasksOnAllNodesFinish(): String = s"Number of joined participants: ${joinedParticipants.size}"
}
