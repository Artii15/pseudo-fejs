package na.przypale.fitter.interactions

import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.{Comment, Post, User, UserContent}
import na.przypale.fitter.repositories.{CommentsCountersRepository, CommentsRepository, PostsCountersRepository}

class CreatingComment(commentsRepository: CommentsRepository,
                      commentsCountersRepository: CommentsCountersRepository,
                      postsCountersRepository: PostsCountersRepository) {
  def create(user: User, userContent: UserContent): Unit = {
    print("Content: ")
    val commentText = CommandLineReader.readString()

    userContent match {
      case comment: Comment =>
        commentsRepository.create(Comment(comment.postAuthor, comment.postTimeId, UUIDs.timeBased(), user.nick, commentText, UUIDs.random(), comment.id))
        commentsCountersRepository.increaseAnswersCounter(comment)
        //commentsCountersRepository.initiateCounters(comment)
      //case Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, content, id, parentId) =>
        //commentsRepository.create(Comment(postAuthor, postTimeId, UUIDs.timeBased(), user.nick, commentText, UUIDs.random(), id))
      case post: Post => //Post(author, timeId, content) =>
        val comment = Comment(post.author, post.timeId, UUIDs.timeBased(), user.nick, commentText, UUIDs.random(), null)
        commentsRepository.create(comment)
        postsCountersRepository.increaseCommentsCounter(post)
        //commentsCountersRepository.initiateCounters(comment)
      case _ =>
    }

  }
}
