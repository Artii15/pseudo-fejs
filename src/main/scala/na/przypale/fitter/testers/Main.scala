package na.przypale.fitter.testers

import akka.actor.{ActorSystem, Props}
import na.przypale.fitter.testers.actors.supervisors.RegistrationTester
import na.przypale.fitter.testers.commands.Start
import na.przypale.fitter.testers.config.RegistrationTesterConfig

object Main {

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem()
    val config = new RegistrationTesterConfig(100, 10)

    val registrationTester = actorSystem.actorOf(Props(new RegistrationTester(config)))
    registrationTester ! Start
  }
}
