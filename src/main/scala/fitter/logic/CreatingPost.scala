package fitter.logic

import com.datastax.driver.core.utils.UUIDs
import fitter.entities.{Post, User}
import fitter.repositories.PostsRepository

class CreatingPost(postsRepository: PostsRepository) {

  def create(content: String, author: User): Unit = {
    postsRepository.create(Post(author.nick, UUIDs.timeBased(), content))
  }
}
