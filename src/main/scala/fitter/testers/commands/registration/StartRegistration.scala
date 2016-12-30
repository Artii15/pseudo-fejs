package fitter.testers.commands.registration

import fitter.testers.commands.nodes.GroupTaskStart

class StartRegistration(val groupSize: Int,
                        val numberOfUniqueNicks: Int,
                        val numberOfWorkersPerNode: Int) extends GroupTaskStart
