package na.przypale.fitter.testers

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import na.przypale.fitter.testers.actors.supervisors.UserActor
import na.przypale.fitter.testers.commands.Start
import na.przypale.fitter.testers.config.SystemConfig

object Main {

  def main(args: Array[String]): Unit = {
    val systemConfig = new SystemConfig(ConfigFactory.load())
    val actorSystem = ActorSystem(systemConfig.actorSystemName)

    val userActor = actorSystem.actorOf(Props[UserActor])
    userActor ! Start
  }
}
