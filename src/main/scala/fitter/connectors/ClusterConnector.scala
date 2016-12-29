package fitter.connectors

import com.datastax.driver.core.Cluster

object ClusterConnector {
  val DEFAULT_CONTACT_POINT = "127.0.0.1"

  def connect(contactPoint: String = DEFAULT_CONTACT_POINT): ((Cluster) => Unit) => Unit = (operations: Cluster => Unit) => {
    val cluster = makeConnection(contactPoint)
    operations(cluster)
    cluster.close()
  }

  def makeConnection(contactPoint: String = DEFAULT_CONTACT_POINT): Cluster =
    Cluster.builder().addContactPoint(contactPoint).build()
}
