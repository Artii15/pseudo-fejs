package na.przypale.fitter.connectors

import com.datastax.driver.core.{Cluster, Session}

object SessionConnector {

  def connect(cluster: Cluster): (String) => ((Session) => Unit) => Unit = (keyspace: String) => (operations: Session => Unit) => {
    val session = makeSession(cluster, keyspace)
    operations(session)
    session.close()
  }

  def makeSession(cluster: Cluster, keyspace: String): Session = cluster.connect(keyspace)
}
