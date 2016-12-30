package fitter.testers.commands.eventsOld

import fitter.testers.commands.nodes.TaskEnd

case class JoiningEventTaskEnd(participants: Iterable[String]) extends TaskEnd
