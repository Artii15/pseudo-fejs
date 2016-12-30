package fitter.testers.actors.workers

import fitter.testers.commands.registration.CreateAccount

class RegistrationWorker extends Worker[CreateAccount, ] {
  override protected def executeTask(taskStart: Nothing): Nothing = ???
}
