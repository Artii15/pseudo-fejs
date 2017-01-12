package fitter.testers.actors.leaders.remote

import akka.actor.Props
import fitter.entities.Post
import fitter.testers.actors.leaders.SessionOwner
import fitter.testers.actors.workers.PostLikeWorker
import fitter.testers.commands.TaskStart
import fitter.testers.commands.posts.{CreateLikers, LikePost}
import fitter.testers.config.SessionConfig
import fitter.testers.results.AggregatedResults
import fitter.testers.results.posts.{PostLiker, PostLikers}

class PostLikesRemoteLeader(sessionConfig: SessionConfig) extends SessionOwner[CreateLikers, PostLiker](sessionConfig) {

  var nicksForWorkers: Iterator[String] = Iterator.empty
  private var postToLike: Option[Post] = None

  override protected val results: AggregatedResults[PostLiker] = new PostLikers()

  override protected def makeWorker(workerId: Int): Props =
    Props(classOf[PostLikeWorker], dependencies.likingUserContent)

  override protected def readTask(task: CreateLikers): Unit = {
    nicksForWorkers = Stream.continually(task.nicks).flatten.iterator
    postToLike = Some(task.post)
  }

  override protected def makeTaskForWorker(workerId: Int): TaskStart = LikePost(postToLike.get, nicksForWorkers.next())
}
