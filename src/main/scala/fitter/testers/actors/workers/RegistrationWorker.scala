package fitter.testers.actors.workers

import java.util.UUID

import fitter.entities.Credentials
import fitter.logic.CreatingUser
import fitter.repositories.exceptions.{UserAlreadyExistsException, UserNotExistsException}
import fitter.testers.commands.registration.CreateAccount
import fitter.testers.results.registration.CreatedAccount

class RegistrationWorker(creatingUser: CreatingUser) extends Worker[CreateAccount, CreatedAccount] {

  override protected def executeTask(task: CreateAccount): CreatedAccount = {
    val credentials = createAccount(task.nick)
    CreatedAccount(credentials)
  }

  private def createAccount(nick: String): Option[Credentials] = {
    try {
      val credentials = Credentials(nick, UUID.randomUUID().toString)
      creatingUser.create(credentials)
      Some(credentials)
    }
    catch {
      case _: UserAlreadyExistsException | _: UserNotExistsException => None
    }
  }
}
