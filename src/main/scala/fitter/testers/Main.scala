package fitter.testers

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import fitter.testers.actors.UserActor
import fitter.testers.commands.Start
import fitter.testers.config.{SessionConfig, SystemConfig}

import scala.collection.JavaConverters

object Main {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val systemConfig = new SystemConfig(
      config.getString("bots.systemName"),
      JavaConverters.collectionAsScalaIterable(config.getStringList("bots.hosts")),
      config.getInt("akka.remote.netty.tcp.port"))
    val sessionConfig = new SessionConfig("test")

    val actorSystem = ActorSystem(systemConfig.actorSystemName)

    val testsSupervisorConfig = new TestsSupervisorConfig(sessionConfig, systemConfig)
    val userActor = actorSystem.actorOf(Props(new UserActor(testsSupervisorConfig)))
    userActor ! Start
  }
}
