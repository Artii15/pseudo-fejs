package fitter.testers.commands.events

import fitter.entities.Event
import fitter.testers.commands.nodes.TaskStart

case class RunParticipants(event: Event, numberOfProcesses: Int) extends TaskStart
