package fitter.testers.commands.events

import fitter.testers.commands.nodes.TaskEnd

case class JoiningEventTaskEnd(participants: Iterable[String]) extends TaskEnd
