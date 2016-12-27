package na.przypale.fitter.testers

import akka.actor.{ActorSystem, Props}
import na.przypale.fitter.{Bootstrap, Dependencies}
import na.przypale.fitter.testers.actors.supervisors.RegistrationTester
import na.przypale.fitter.testers.commands.Start
import na.przypale.fitter.testers.config.RegistrationTesterConfig

object Main {
  def main(args: Array[String]): Unit = Bootstrap.start(start)

  private def start(dependencies: Dependencies): Unit = {
    val actorSystem = ActorSystem()
    val config = new RegistrationTesterConfig(1, 10)

    val registrationTester = actorSystem.actorOf(Props(new RegistrationTester(config, dependencies.creatingUser)))
    registrationTester ! Start
  }
}
