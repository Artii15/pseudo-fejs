package na.przypale.fitter.logic

import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.entities.{Post, User}
import na.przypale.fitter.repositories.PostsRepository

class CreatingPost(postsRepository: PostsRepository) {

  def create(content: String, author: User): Unit = {
    postsRepository.create(Post(author.nick, UUIDs.timeBased(), content))
  }
}
