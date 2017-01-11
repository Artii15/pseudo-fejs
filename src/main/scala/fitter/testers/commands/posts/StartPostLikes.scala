package fitter.testers.commands.posts

import fitter.testers.commands.GroupTaskStart

class StartPostLikes(val groupSize: Int,
                     val numberOfWorkersPerNode: Int,
                     val postAuthor: String,
                     val numberOfUniqueUsers: Int) extends GroupTaskStart
