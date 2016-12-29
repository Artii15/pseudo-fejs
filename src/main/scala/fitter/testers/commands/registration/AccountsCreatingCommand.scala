package fitter.testers.commands.registration

import fitter.testers.commands.nodes.TaskStart

case class AccountsCreatingCommand(numberOfProcesses: Int, nicks: Iterable[String]) extends TaskStart
