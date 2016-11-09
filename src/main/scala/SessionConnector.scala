import com.datastax.driver.core.{Cluster, Session}

object SessionConnector {

  def makeConnector(cluster: Cluster) = (keyspace: String) => (operations: Session => Unit) => {
    val session = cluster.connect(keyspace)
    operations(session)
    session.close()
  }

}
