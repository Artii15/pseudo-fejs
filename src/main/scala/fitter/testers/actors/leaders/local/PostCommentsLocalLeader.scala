package fitter.testers.actors.leaders.local

import java.util.UUID

import akka.actor.{Deploy, Props}
import com.datastax.driver.core.utils.UUIDs
import fitter.entities.{Credentials, Post}
import fitter.testers.actors.leaders.SessionOwner
import fitter.testers.actors.leaders.remote.PostCommentsRemoteLeader
import fitter.testers.commands.TaskStart
import fitter.testers.commands.posts.{CreateCommenters, StartPostComments}
import fitter.testers.config.SessionConfig
import fitter.testers.generators.RandomStringsGenerator
import fitter.testers.results.AggregatedResults
import fitter.testers.results.posts.{PostCommenters, PostCommentsReport}

import scala.collection.mutable.ListBuffer

class PostCommentsLocalLeader(deploys: Iterator[Deploy], sessionConfig: SessionConfig)
  extends SessionOwner[StartPostComments, PostCommenters](sessionConfig){

  private val accountsNicks: ListBuffer[String] = ListBuffer.empty
  private var numberOfWorkersPerNode = 0
  private var post: Option[Post] = None

  override protected val results: AggregatedResults[PostCommenters] = new PostCommentsReport()

  override protected def makeWorker(workerId: Int): Props =
    Props(classOf[PostCommentsRemoteLeader], sessionConfig).withDeploy(deploys.next())

  override protected def readTask(task: StartPostComments): Unit = {
    numberOfWorkersPerNode = task.numberOfWorkersPerNode
    accountsNicks.clear()
    Stream.from(0).take(task.numberOfUniqueUsers).foreach(_ => {
      accountsNicks += RandomStringsGenerator.generateRandomString()
    })
    accountsNicks.foreach(nick => dependencies.creatingUser.create(Credentials(nick, UUID.randomUUID().toString)))
    post = Some(Post(task.postAuthor, UUIDs.timeBased(), "Comments Test"))
    dependencies.postsRepository.create(post.get)
  }

  override protected def makeTaskForWorker(workerId: Int): TaskStart =
    new CreateCommenters(numberOfWorkersPerNode, post.get, accountsNicks)
}
