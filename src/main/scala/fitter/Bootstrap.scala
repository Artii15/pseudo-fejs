package fitter

import com.datastax.driver.core.Cluster
import fitter.connectors.{ClusterConnector, SessionConnector}

object Bootstrap {

  def start(appEntryPoint: (Dependencies => Unit)): Unit = {
    connectToCluster(cluster => {
      SessionConnector.connect(cluster)("test")(session => {
        appEntryPoint(new Dependencies(session))
      })
    })
  }

  def connectToCluster(appEntryPoint: (Cluster => Unit)): Unit = {
    ClusterConnector.connect()(cluster => appEntryPoint(cluster)) //127.0.0.1
  }
}
