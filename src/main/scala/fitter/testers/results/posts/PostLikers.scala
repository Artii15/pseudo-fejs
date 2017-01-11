package fitter.testers.results.posts

import fitter.entities.Credentials
import fitter.testers.results.AggregatedResults

import scala.collection.mutable.ListBuffer

class PostLikers extends AggregatedResults[PostLiker]{

  val likers: ListBuffer[Credentials] = ListBuffer.empty

  override def combine(liker: PostLiker): Unit = {
    val PostLiker(credentials) = liker
    if(credentials.isDefined) likers += credentials.get
  }

  override def clear(): Unit = likers.clear()
}
