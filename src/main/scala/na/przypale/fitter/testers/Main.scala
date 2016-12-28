package na.przypale.fitter.testers

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import na.przypale.fitter.Dependencies
import na.przypale.fitter.connectors.{ClusterConnector, SessionConnector}
import na.przypale.fitter.testers.actors.supervisors.UserActor
import na.przypale.fitter.testers.commands.Start
import na.przypale.fitter.testers.config.{RegistrationTesterConfig, SystemConfig}

object Main {

  def main(args: Array[String]): Unit = {
    val hosts = List("192.168.1.8", "192.168.1.9")
    val connections = hosts.map(addresses => ClusterConnector.makeConnection(addresses))
    val sessions = connections.map(connection => SessionConnector.makeSession(connection, "test"))

    val systemConfig = new SystemConfig(ConfigFactory.load())
    val actorSystem = ActorSystem(systemConfig.actorSystemName)
    actorSystem.registerOnTermination {
      sessions.foreach(_.close())
      connections.foreach(_.close())
    }

    val config = new RegistrationTesterConfig(500, 10)
    val hostsDependencies = hosts.zip(sessions).map { case (address, session) =>
      HostsDependencies(s"akka.tcp://${systemConfig.actorSystemName}@$address:2552", new Dependencies(session))
    }
    val registrationTester = actorSystem.actorOf(Props(new UserActor(config, hostsDependencies)))
    registrationTester ! Start
  }
}
