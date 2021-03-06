package fitter.testers.results.posts

import fitter.testers.results.{AggregatedResults, TaskReport}

import scala.collection.mutable.ListBuffer

class PostLikesReport extends AggregatedResults[PostLikers] with TaskReport{

  val accounts: ListBuffer[String] = ListBuffer.empty

  override def combine(postLikers: PostLikers): Unit = accounts ++= postLikers.likers

  override def clear(): Unit = accounts.clear()
}
