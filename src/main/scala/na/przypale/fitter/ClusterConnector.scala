package na.przypale.fitter

import com.datastax.driver.core.Cluster

object ClusterConnector {
  val DEFAULT_CONTACT_POINT = "127.0.0.1"

  def connect(contactPoint: String = DEFAULT_CONTACT_POINT) = (operations: Cluster => Unit) => {
    val cluster = Cluster.builder().addContactPoint(contactPoint).build()
    operations(cluster)
    cluster.close()
  }
}
