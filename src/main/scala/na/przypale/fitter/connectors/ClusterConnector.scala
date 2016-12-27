package na.przypale.fitter.connectors

import com.datastax.driver.core.Cluster

object ClusterConnector {
  val DEFAULT_CONTACT_POINT = "127.0.0.1"

  def connect(contactPoint: String = DEFAULT_CONTACT_POINT): ((Cluster) => Unit) => Unit = (operations: Cluster => Unit) => {
    val cluster = makeCluster(contactPoint)
    operations(cluster)
    cluster.close()
  }

  def makeCluster(contactPoint: String = DEFAULT_CONTACT_POINT): Cluster =
    Cluster.builder().addContactPoint(contactPoint).build()
}
