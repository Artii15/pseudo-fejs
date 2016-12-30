package fitter.testers.commands.events

import fitter.entities.Event
import fitter.testers.commands.GroupTaskStart

class MakeParticipants(val groupSize: Int, val event: Event) extends GroupTaskStart
