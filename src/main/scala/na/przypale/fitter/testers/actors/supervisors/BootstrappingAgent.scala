package na.przypale.fitter.testers.actors.supervisors

import akka.actor.{Actor, ActorRef, PoisonPill}
import na.przypale.fitter.Dependencies
import na.przypale.fitter.connectors.{ClusterConnector, SessionConnector}
import na.przypale.fitter.testers.commands.{TaskEnd, TaskStart}
import na.przypale.fitter.testers.commands.nodes.{Deployment, Kill}
import na.przypale.fitter.testers.config.SessionConfig

import scala.collection.mutable

class BootstrappingAgent(config: SessionConfig) extends Actor {

  private val cluster = ClusterConnector.makeConnection(config.contactPoint)
  private val session = SessionConnector.makeSession(cluster, config.keyspace)
  private val dependencies = new Dependencies(session)

  private val deployedActors: mutable.Map[String, ActorRef] = mutable.Map.empty

  override def postStop(): Unit = {
    session.close()
    cluster.close()
  }

  override def receive: Receive = {
    case deployment: Deployment => deploy(deployment)
    case Kill(id) => kill(id)
    case taskStart @ TaskStart(deploymentId) => deployedActors(deploymentId) ! taskStart
    case taskEnd: TaskEnd => context.parent ! taskEnd
  }

  private def deploy(deployment: Deployment): Unit = {
    val actor = context.actorOf(deployment.generateProps(dependencies))
    deployedActors += (deployment.id -> actor)
  }

  private def kill(id: String): Unit = {
    deployedActors(id) ! PoisonPill
    deployedActors -= id
  }
}
