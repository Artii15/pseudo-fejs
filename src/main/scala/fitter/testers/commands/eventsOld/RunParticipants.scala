package fitter.testers.commands.eventsOld

import fitter.entities.Event
import fitter.testers.commands.nodes.TaskStart

case class RunParticipants(event: Event, numberOfProcesses: Int) extends TaskStart
