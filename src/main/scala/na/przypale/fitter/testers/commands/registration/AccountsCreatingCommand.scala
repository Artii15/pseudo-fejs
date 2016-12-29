package na.przypale.fitter.testers.commands.registration

import na.przypale.fitter.testers.commands.nodes.TaskStart

case class AccountsCreatingCommand(numberOfProcesses: Int, nicks: Iterable[String]) extends TaskStart
