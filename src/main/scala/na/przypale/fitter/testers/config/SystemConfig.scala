package na.przypale.fitter.testers.config

import com.typesafe.config.Config

class SystemConfig(config: Config) {
  val actorSystemName: String = config.getString("bots.systemName")
}
