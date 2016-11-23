package na.przypale.fitter.interactions

import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.{Comment, Post, User}
import na.przypale.fitter.repositories.CommentsRepository

class CreatingComment(commentsRepository: CommentsRepository) {
  def create(user: User, post: Post): Unit = {
    print("Content: ")
    val content = CommandLineReader.readString()

    commentsRepository.create(Comment(post.author, post.timeId, UUIDs.timeBased(), user.nick, content, UUIDs.timeBased(), null))
  }
}
