package fitter.testers.results.posts

import fitter.testers.results.AggregatedResults

import scala.collection.mutable.ListBuffer

class PostCommenters extends AggregatedResults[PostCommenter] {

  val commenters: ListBuffer[String] = ListBuffer.empty

  override def combine(commenter: PostCommenter): Unit = {
    val PostCommenter(nick) = commenter
    if(nick.isDefined) commenters += nick.get
  }

  override def clear(): Unit = commenters.clear()
}
