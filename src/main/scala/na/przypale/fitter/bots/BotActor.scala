package na.przypale.fitter.bots

import akka.actor.Actor
import na.przypale.fitter.bots.commands.Start

class BotActor extends Actor {
  override def receive: Receive = {
    case Start => enterTheSystem()
  }

  private def enterTheSystem(): Unit = {

  }

  //private def register()
}
