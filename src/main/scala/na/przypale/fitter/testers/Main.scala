package na.przypale.fitter.testers

import akka.actor.{ActorSystem, Props}
import com.datastax.driver.core.Cluster
import na.przypale.fitter.Bootstrap
import na.przypale.fitter.testers.actors.supervisors.RegistrationTester
import na.przypale.fitter.testers.commands.Start
import na.przypale.fitter.testers.config.RegistrationTesterConfig

object Main {

  def main(args: Array[String]): Unit = Bootstrap.connectToCluster(start)

  private def start(cluster: Cluster): Unit = {
    val actorSystem = ActorSystem()
    val config = new RegistrationTesterConfig(1, 10)


    val registrationTester = actorSystem.actorOf(Props(new RegistrationTester(config, cluster)))
    registrationTester ! Start
  }
}
