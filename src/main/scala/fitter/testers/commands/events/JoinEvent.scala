package fitter.testers.commands.events

import fitter.entities.Event
import fitter.testers.commands.nodes.TaskStart

case class JoinEvent(event: Event) extends TaskStart
