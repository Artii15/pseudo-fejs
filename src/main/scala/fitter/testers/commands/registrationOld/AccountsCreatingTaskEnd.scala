package fitter.testers.commands.registrationOld

import fitter.entities.Credentials
import fitter.testers.commands.nodes.TaskEnd

case class AccountsCreatingTaskEnd(createdAccountsCredentials: Iterable[Credentials]) extends TaskEnd
