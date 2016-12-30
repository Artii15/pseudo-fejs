package fitter.testers.commands.events

import fitter.testers.commands.nodes.GroupTaskStart

class StartEvent(val groupSize: Int,
                 numberOfWorkersPerNode: Int,
                 eventAuthor: String,
                 maxNumberOfEventParticipants: Int) extends GroupTaskStart
