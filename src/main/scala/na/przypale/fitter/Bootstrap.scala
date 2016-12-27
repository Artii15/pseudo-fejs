package na.przypale.fitter

import na.przypale.fitter.connectors.{ClusterConnector, SessionConnector}

object Bootstrap {

  def start(appEntryPoint: (Dependencies => Unit)): Unit = {
    ClusterConnector.connect("127.0.0.1")(cluster => {
      SessionConnector.connect(cluster)("test")(session => {
        appEntryPoint(new Dependencies(session))
      })
    })
  }
}
