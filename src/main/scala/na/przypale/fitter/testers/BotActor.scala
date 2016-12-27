package na.przypale.fitter.testers

import java.util.UUID

import akka.actor.Actor
import com.thedeanda.lorem.LoremIpsum
import na.przypale.fitter.bots.commands.{PostWritingCommand, Start}
import na.przypale.fitter.entities.{Credentials, User}
import na.przypale.fitter.logic.exceptions.AuthenticationException
import na.przypale.fitter.logic.{Authenticating, CreatingPost, CreatingUser}
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException

import scala.annotation.tailrec

class BotActor(creatingUser: CreatingUser,
               authenticating: Authenticating,
               creatingPost: CreatingPost,
               config: Config) extends Actor {

  private val loremIpsum = LoremIpsum.getInstance()

  override def receive: Receive = {
    case Start => gainAccessToSystem()
    case PostWritingCommand(loggedUser) => writeRandomPost(loggedUser)
  }

  @tailrec
  private def gainAccessToSystem(): Unit = {
    try {
      val registeredUserCredentials = register()
      val loggedUser = logIn(registeredUserCredentials)
      self ! PostWritingCommand(loggedUser)
    }
    catch {
      case _: AuthenticationException => gainAccessToSystem()
    }
  }

  @tailrec
  private def register(): Credentials = {
    val credentials = Credentials(UUID.randomUUID().toString, UUID.randomUUID().toString)
    try {
      creatingUser.create(credentials)
      credentials
    }
    catch {
      case _: UserAlreadyExistsException => register()
    }
  }

  private def logIn(credentials: Credentials): User = authenticating.authenticate(credentials)

  private def writeRandomPost(loggedUser: User): Unit = {
    val postContent = loremIpsum.getParagraphs(config.minParagraphsPerPost, config.maxParagraphsPerPost)
    creatingPost.create(postContent, loggedUser)
    self ! PostWritingCommand(loggedUser)
  }
}
