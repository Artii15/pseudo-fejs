package na.przypale.fitter.testers.commands.registration

import na.przypale.fitter.entities.Credentials
import na.przypale.fitter.testers.commands.nodes.TaskEnd

case class AccountsCreatingTaskEnd(createdAccountsCredentials: Iterable[Credentials]) extends TaskEnd
