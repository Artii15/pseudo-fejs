package na.przypale.fitter.interactions

import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.{Post, User, UserContent}
import na.przypale.fitter.repositories.PostsRepository

class CreatingPost(postsRepository: PostsRepository) {
  def create(user: User): Unit = {
    print("Content: ")
    val content = CommandLineReader.readString()

    postsRepository.create(Post(UserContent(user.nick, UUIDs.timeBased(), content)))
  }
}
