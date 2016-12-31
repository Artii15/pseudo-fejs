package fitter.testers.actors.leaders

import com.datastax.driver.core.{Cluster, Session}
import fitter.Dependencies
import fitter.connectors.{ClusterConnector, SessionConnector}
import fitter.testers.commands.GroupTaskStart
import fitter.testers.config.SessionConfig

import scala.reflect.ClassTag

abstract class SessionOwner[T <: GroupTaskStart: ClassTag, U: ClassTag](sessionConfig: SessionConfig)
  extends Leader[T, U] {

  private val cluster: Cluster = ClusterConnector.makeConnection(sessionConfig.contactPoint)
  private val session: Session = SessionConnector.makeSession(cluster, sessionConfig.keyspace)
  protected val dependencies: Dependencies = new Dependencies(session)

  override def postStop(): Unit = {
    session.close()
    cluster.close()
    super.postStop()
  }
}
