package na.przypale.fitter.testers.actors.supervisors

import akka.actor.{Actor, PoisonPill}
import na.przypale.fitter.Dependencies
import na.przypale.fitter.connectors.{ClusterConnector, SessionConnector}
import na.przypale.fitter.testers.commands.nodes.{Deployment, TaskEnd, TaskStart}
import na.przypale.fitter.testers.config.SessionConfig

class BootstrappingAgent(config: SessionConfig) extends Actor {

  private val cluster = ClusterConnector.makeConnection(config.contactPoint)
  private val session = SessionConnector.makeSession(cluster, config.keyspace)
  private val dependencies = new Dependencies(session)

  override def postStop(): Unit = {
    session.close()
    cluster.close()
  }

  override def receive: Receive = {
    case deployment: Deployment => deploy(deployment)
  }

  private def deploy(deployment: Deployment): Unit = {
    context.actorOf(deployment.generateProps(dependencies))
    context.become(deployed)
  }

  private def deployed: Receive = {
    case taskStart: TaskStart => beginTask(taskStart)
  }

  private def beginTask(taskStart: TaskStart): Unit = {
    context.children.foreach(_ ! taskStart)
    context.become(waitingForTaskFinish)
  }

  private def waitingForTaskFinish: Receive = {
    case taskEnd: TaskEnd => finishCurrentTask(taskEnd)
  }

  private def finishCurrentTask(taskEnd: TaskEnd): Unit = {
    context.children.foreach(_ ! PoisonPill)
    context.parent ! taskEnd
    context.become(receive)
  }
}
