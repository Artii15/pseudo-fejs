package fitter.testers.actors.supervisors

import akka.actor.Props
import fitter.Dependencies
import fitter.testers.commands.nodes.{Deployment, TaskEnd, TaskStart}
import fitter.testers.config.TestsSupervisorConfig

class EventsJoiningSupervisor(testsSupervisorConfig: TestsSupervisorConfig)
  extends TestsSupervisor(testsSupervisorConfig) {

  private val deployment = Deployment((dependencies: Dependencies) => {
    Props(classOf[])
  })

  override protected def generateDeployment(): Deployment = deployment

  override protected def generateTaskStart(): TaskStart = ???

  override protected def onTaskEndOnSingleNode(taskEnd: TaskEnd): Unit = ???

  override protected def onTasksOnAllNodesFinish(): String = ???
}
