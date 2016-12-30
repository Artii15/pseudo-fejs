package fitter.testers.actors.workers

import java.util.UUID

import fitter.entities.Credentials
import fitter.logic.CreatingUser
import fitter.repositories.exceptions.{UserAlreadyExistsException, UserNotExistsException}
import fitter.testers.commands.registration.CreateAccount
import fitter.testers.results.registration.AccountCreatingResult

class RegistrationWorker(creatingUser: CreatingUser) extends Worker[CreateAccount, AccountCreatingResult] {
  override protected def executeTask(task: CreateAccount): AccountCreatingResult = {
    val credentials = Credentials(task.nick, UUID.randomUUID().toString)
    try {
      creatingUser.create(credentials)
      AccountCreatingResult(Some(credentials))
    }
    catch {
      case _: UserAlreadyExistsException | _: UserNotExistsException => AccountCreatingResult(None)
    }

  }
}
