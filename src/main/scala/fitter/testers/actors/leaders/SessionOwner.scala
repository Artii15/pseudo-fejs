package fitter.testers.actors.leaders

import com.datastax.driver.core.{Cluster, Session}
import fitter.Dependencies
import fitter.connectors.{ClusterConnector, SessionConnector}
import fitter.testers.config.SessionConfig

abstract class SessionOwner(sessionConfig: SessionConfig) extends Leader {

  private val cluster: Cluster = ClusterConnector.makeConnection(sessionConfig.contactPoint)
  private val session: Session = SessionConnector.makeSession(cluster, sessionConfig.keyspace)
  protected val dependencies: Dependencies = new Dependencies(session)

  override def postStop(): Unit = {
    session.close()
    cluster.close()
    super.postStop()
  }
}
