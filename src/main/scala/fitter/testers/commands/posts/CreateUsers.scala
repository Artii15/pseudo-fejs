package fitter.testers.commands.posts

import fitter.entities.Post
import fitter.testers.commands.GroupTaskStart

class CreateUsers(val groupSize: Int, val post: Post, val nicks: Iterable[String]) extends GroupTaskStart