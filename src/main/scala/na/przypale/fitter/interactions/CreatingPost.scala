package na.przypale.fitter.interactions

import java.util.Date

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.{Post, User}
import na.przypale.fitter.repositories.PostsRepository

class CreatingPost(postsRepository: PostsRepository) {
  def create(user: User): Unit = {
    print("Content: ")
    val content = CommandLineReader.readString()

    postsRepository.create(Post(user.nick, new Date(), content))
  }
}
