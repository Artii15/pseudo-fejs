package fitter.testers.actors.supervisors

import akka.actor.{Actor, Props}
import fitter.testers.actors.DeployGenerator
import fitter.testers.commands.{Finish, Start}
import fitter.testers.commands.nodes.TaskEnd
import fitter.testers.config.{SessionConfig, SystemConfig}

abstract class TestsSupervisor(systemConfig: SystemConfig, sessionConfig: SessionConfig) extends Actor {

  private var workingNodes = 0
  private val numberOfNodes = systemConfig.nodesAddresses.size

  override def preStart(): Unit = {
    systemConfig.nodesAddresses.foreach(address => {
      val deploy = DeployGenerator.makeRemoteDeploy(systemConfig.actorSystemName, address, systemConfig.nodesPort)
      context.actorOf(Props(classOf[BootstrappingAgent], sessionConfig).withDeploy(deploy))
    })
  }

  override def receive: Receive = {
    case Start => receiveStart()
  }

  private def receiveStart(): Unit = {
    run()
    workingNodes = numberOfNodes
    context.become(waitingForTasksFinish)
  }

  protected def run(): Unit

  private def waitingForTasksFinish: Receive = {
    case taskEnd: TaskEnd => receiveTaskEnd(taskEnd)
  }

  private def receiveTaskEnd(taskEnd: TaskEnd) = {
    handleTaskEnd(taskEnd)
    workingNodes -= 1
    if(workingNodes == 0) {
      onAllTasksFinish()
      context.parent ! Finish
    }
  }

  protected def handleTaskEnd(taskEnd: TaskEnd): Unit

  protected def onAllTasksFinish(): Unit
}
