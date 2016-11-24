package na.przypale.fitter.interactions

import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.{Comment, Post, User, UserContent}
import na.przypale.fitter.repositories.CommentsRepository

class CreatingComment(commentsRepository: CommentsRepository) {
  def create(user: User, userContent: UserContent): Unit = {
    print("Content: ")
    val commentText = CommandLineReader.readString()

    userContent match {
      case Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, content, id, parentId) =>
        commentsRepository.create(Comment(postAuthor, postTimeId, UUIDs.timeBased(), user.nick, commentText, UUIDs.timeBased(), id))
      case Post(author, timeId, content) =>
        commentsRepository.create(Comment(author, timeId, UUIDs.timeBased(), user.nick, commentText, UUIDs.timeBased(), null))
      case _ =>
    }

  }
}
