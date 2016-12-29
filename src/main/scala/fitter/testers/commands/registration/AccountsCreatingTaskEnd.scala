package fitter.testers.commands.registration

import fitter.entities.Credentials
import fitter.testers.commands.nodes.TaskEnd

case class AccountsCreatingTaskEnd(createdAccountsCredentials: Iterable[Credentials]) extends TaskEnd
