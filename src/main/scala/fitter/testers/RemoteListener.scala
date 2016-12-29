package fitter.testers

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object RemoteListener {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val actorSystem = ActorSystem(config.getString("bots.systemName"))
    StdIn.readLine()
    actorSystem.terminate()
  }
}
