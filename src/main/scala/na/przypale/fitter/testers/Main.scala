package na.przypale.fitter.testers

import akka.actor.{ActorSystem, Props}
import na.przypale.fitter.Dependencies
import na.przypale.fitter.connectors.{ClusterConnector, SessionConnector}
import na.przypale.fitter.testers.actors.supervisors.RegistrationTester
import na.przypale.fitter.testers.commands.Start
import na.przypale.fitter.testers.config.RegistrationTesterConfig

object Main {

  def main(args: Array[String]): Unit = {
    val cluster = ClusterConnector.makeCluster()
    val session = SessionConnector.makeSession(cluster, "test")

    val actorSystem = ActorSystem()
    actorSystem.registerOnTermination { session.close(); cluster.close() }

    val config = new RegistrationTesterConfig(100, 10)
    val registrationTester = actorSystem.actorOf(Props(new RegistrationTester(config, new Dependencies(session))))
    registrationTester ! Start
  }
}
