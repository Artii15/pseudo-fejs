package fitter.testers.commands.events

import fitter.testers.commands.GroupTaskStart

class StartEvent(val groupSize: Int,
                 val numberOfWorkersPerNode: Int,
                 val eventAuthor: String,
                 val maxNumberOfEventParticipants: Int) extends GroupTaskStart
