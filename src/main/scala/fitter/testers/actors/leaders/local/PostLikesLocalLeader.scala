package fitter.testers.actors.leaders.local

import java.util.UUID

import akka.actor.{Deploy, Props}
import com.datastax.driver.core.utils.UUIDs
import fitter.entities.{Credentials, Post}
import fitter.testers.actors.leaders.SessionOwner
import fitter.testers.actors.leaders.remote.PostLikesRemoteLeader
import fitter.testers.commands.TaskStart
import fitter.testers.commands.posts.{CreateUsers, StartPostLikes}
import fitter.testers.config.SessionConfig
import fitter.testers.generators.RandomStringsGenerator
import fitter.testers.results.AggregatedResults
import fitter.testers.results.posts.{PostLikers, PostLikesReport}

import scala.collection.mutable.ListBuffer

class PostLikesLocalLeader(deploys: Iterator[Deploy], sessionConfig: SessionConfig)
  extends SessionOwner[StartPostLikes, PostLikers](sessionConfig) {

  private val accountsNicks: ListBuffer[String] = ListBuffer.empty
  private var numberOfWorkersPerNode = 0
  private var post: Option[Post] = None

  override protected val results: AggregatedResults[PostLikers] = new PostLikesReport()

  override protected def makeWorker(workerId: Int): Props =
    Props(classOf[PostLikesRemoteLeader], sessionConfig).withDeploy(deploys.next())

  override protected def readTask(task: StartPostLikes): Unit = {
    numberOfWorkersPerNode = task.numberOfWorkersPerNode
    accountsNicks.clear()
    Stream.from(0).take(task.numberOfUniqueUsers).foreach(_ => {
      accountsNicks += RandomStringsGenerator.generateRandomString()
    })
    accountsNicks.foreach(nick => dependencies.creatingUser.create(Credentials(nick, UUID.randomUUID().toString)))
    post = Some(Post(task.postAuthor, UUIDs.timeBased(), "Like Test"))
    dependencies.postsRepository.create(post.get)
  }

  override protected def makeTaskForWorker(workerId: Int): TaskStart =
    new CreateUsers(numberOfWorkersPerNode, post.get, accountsNicks)
}
