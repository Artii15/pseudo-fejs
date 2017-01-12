package fitter.testers.actors.workers

import com.datastax.driver.core.utils.UUIDs
import fitter.entities.Comment
import fitter.repositories.{CommentsRepository, PostsCountersRepository}
import fitter.testers.commands.posts.CommentPost
import fitter.testers.generators.RandomStringsGenerator
import fitter.testers.results.posts.PostCommenter

class PostCommentWorker(commentsRepository: CommentsRepository, postsCountersRepository: PostsCountersRepository)
  extends Worker[CommentPost, PostCommenter]{

  override protected def executeTask(task: CommentPost): PostCommenter = {
    val comment = Comment(task.post.author, task.post.timeId, UUIDs.timeBased(), task.nick, RandomStringsGenerator.generateRandomString(), UUIDs.random(), "")
    commentsRepository.create(comment)
    postsCountersRepository.increaseCommentsCounter(task.post)
    PostCommenter(Some(task.nick))
  }
}
