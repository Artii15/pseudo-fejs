package fitter.testers.commands.registration

import fitter.testers.commands.GroupTaskStart

class CreateAccounts(val groupSize: Int, val nicks: Iterable[String]) extends GroupTaskStart
