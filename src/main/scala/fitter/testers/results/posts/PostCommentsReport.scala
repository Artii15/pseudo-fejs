package fitter.testers.results.posts

import fitter.testers.results.{AggregatedResults, TaskReport}

import scala.collection.mutable.ListBuffer

class PostCommentsReport extends AggregatedResults[PostCommenters] with TaskReport {

  val accounts: ListBuffer[String] = ListBuffer.empty

  override def combine(postCommenters: PostCommenters): Unit = accounts ++= postCommenters.commenters

  override def clear(): Unit = accounts.clear()
}
