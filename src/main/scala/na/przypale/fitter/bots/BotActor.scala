package na.przypale.fitter.bots

import java.util.UUID

import akka.actor.Actor
import na.przypale.fitter.bots.commands.Start
import na.przypale.fitter.entities.Credentials
import na.przypale.fitter.logic.{Authenticating, CreatingUser}
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException

import scala.annotation.tailrec

class BotActor(creatingUser: CreatingUser, authenticating: Authenticating) extends Actor {
  private var credentials: Credentials = generateCredentials()

  override def receive: Receive = {
    case Start => enterTheSystem()
  }

  private def enterTheSystem(): Unit = {
    register()
  }

  @tailrec
  private def register(): Unit = {
    try {
      creatingUser.create(credentials)
    }
    catch {
      case _: UserAlreadyExistsException => credentials = generateCredentials(); register()
    }
  }

  private def generateCredentials(): Credentials = Credentials(UUID.randomUUID().toString, UUID.randomUUID().toString)
}
