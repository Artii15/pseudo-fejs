package fitter.testers.actors.supervisors

import akka.actor.{Actor, Props}
import fitter.testers.actors.DeployGenerator
import fitter.testers.commands.{Finish, Start}
import fitter.testers.commands.nodes.{Deployment, TaskEnd, TaskStart}
import fitter.testers.config.TestsSupervisorConfig

abstract class TestsSupervisor(config: TestsSupervisorConfig) extends Actor {

  private var workingNodes = 0
  private val numberOfNodes = config.systemConfig.nodesAddresses.size

  override def preStart(): Unit = {
    val systemConfig = config.systemConfig
    systemConfig.nodesAddresses.foreach(address => {
      val deploy = DeployGenerator.makeRemoteDeploy(systemConfig.actorSystemName, address, systemConfig.nodesPort)
      context.actorOf(Props(classOf[BootstrappingAgent], config.sessionConfig).withDeploy(deploy))
    })
  }

  override def receive: Receive = {
    case Start => receiveStart()
  }

  private def receiveStart(): Unit = {
    workingNodes = numberOfNodes
    context.children.foreach(agent => {
      agent ! generateDeployment()
      agent ! generateTaskStart()
    })
    context.become(waitingForTasksFinish)
  }

  protected def generateDeployment(): Deployment

  protected def generateTaskStart(): TaskStart

  private def waitingForTasksFinish: Receive = {
    case taskEnd: TaskEnd => receiveTaskEnd(taskEnd)
  }

  private def receiveTaskEnd(taskEnd: TaskEnd) = {
    onTaskEndOnSingleNode(taskEnd)
    workingNodes -= 1
    if(workingNodes == 0) {
      val report = onTasksOnAllNodesFinish()
      context.parent ! Finish(report)
    }
  }

  protected def onTaskEndOnSingleNode(taskEnd: TaskEnd): Unit

  protected def onTasksOnAllNodesFinish(): String
}
