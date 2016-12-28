package na.przypale.fitter.testers

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import na.przypale.fitter.testers.config.SystemConfig

import scala.io.StdIn

object RemoteListener {
  def main(args: Array[String]): Unit = {
    val config = new SystemConfig(ConfigFactory.load())
    val actorSystem = ActorSystem(config.actorSystemName)

    StdIn.readLine()
    actorSystem.terminate()
  }
}
