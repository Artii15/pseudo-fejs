package fitter.testers.results.posts

import fitter.testers.results.AggregatedResults

import scala.collection.mutable.ListBuffer

class PostLikers extends AggregatedResults[PostLiker]{

  val likers: ListBuffer[String] = ListBuffer.empty

  override def combine(liker: PostLiker): Unit = {
    val PostLiker(nick) = liker
    if(nick.isDefined) likers += nick.get
  }

  override def clear(): Unit = likers.clear()
}
