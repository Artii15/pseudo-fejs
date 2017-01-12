package fitter.testers.actors.leaders.remote

import akka.actor.Props
import fitter.entities.Post
import fitter.testers.actors.leaders.SessionOwner
import fitter.testers.actors.workers.PostCommentWorker
import fitter.testers.commands.TaskStart
import fitter.testers.commands.posts.{CommentPost, CreateCommenters}
import fitter.testers.config.SessionConfig
import fitter.testers.results.AggregatedResults
import fitter.testers.results.posts.{PostCommenter, PostCommenters}

class PostCommentsRemoteLeader(sessionConfig: SessionConfig) extends SessionOwner[CreateCommenters, PostCommenter](sessionConfig) {

  var nicksForWorkers: Iterator[String] = Iterator.empty
  private var postToComment: Option[Post] = None

  override protected val results: AggregatedResults[PostCommenter] = new PostCommenters()

  override protected def makeWorker(workerId: Int): Props =
    Props(classOf[PostCommentWorker], dependencies.commentsRepository, dependencies.postsCountersRepository)

  override protected def readTask(task: CreateCommenters): Unit = {
    nicksForWorkers = Stream.continually(task.nicks).flatten.iterator
    postToComment = Some(task.post)
  }

  override protected def makeTaskForWorker(workerId: Int): TaskStart = CommentPost(postToComment.get, nicksForWorkers.next())
}
