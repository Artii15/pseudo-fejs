package fitter.testers.actors.workers

import fitter.testers.commands.registration.CreateAccount
import fitter.testers.results.registration.AccountCreatingResult

class RegistrationWorker extends Worker[CreateAccount, AccountCreatingResult] {
  override protected def executeTask(taskStart: CreateAccount): AccountCreatingResult = {
    AccountCreatingResult(None)
  }
}
