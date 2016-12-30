package fitter.testers.commands.events

import fitter.entities.Event
import fitter.testers.commands.TaskStart

case class JoinEvent(event: Event) extends TaskStart
