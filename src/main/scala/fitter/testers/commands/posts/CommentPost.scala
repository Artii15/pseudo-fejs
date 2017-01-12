package fitter.testers.commands.posts

import fitter.entities.Post
import fitter.testers.commands.TaskStart

case class CommentPost(post: Post, val nick: String) extends TaskStart
