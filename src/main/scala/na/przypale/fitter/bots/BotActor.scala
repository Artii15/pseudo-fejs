package na.przypale.fitter.bots

import java.util.UUID

import akka.actor.Actor
import na.przypale.fitter.bots.commands.Start
import na.przypale.fitter.entities.Credentials
import na.przypale.fitter.logic.CreatingUser

class BotActor(creatingUser: CreatingUser) extends Actor {
  private val credentials = Credentials(UUID.randomUUID().toString, UUID.randomUUID().toString)

  override def receive: Receive = {
    case Start => enterTheSystem()
  }

  private def enterTheSystem(): Unit = {

  }

  private def register(): Unit = {
    creatingUser.create(credentials)
  }
}
