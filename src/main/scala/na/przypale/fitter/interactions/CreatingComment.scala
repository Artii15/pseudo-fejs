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
        commentsRepository.create(Comment(comment.postAuthor, comment.postTimeId, UUIDs.timeBased(), user.nick, commentText, UUIDs.random(), comment.id.toString))
        commentsCountersRepository.increaseAnswersCounter(comment)
      case post: Post =>
        val comment = Comment(post.author, post.timeId, UUIDs.timeBased(), user.nick, commentText, UUIDs.random(), "")
        commentsRepository.create(comment)
        postsCountersRepository.increaseCommentsCounter(post)
      case _ =>
    }

  }
}
