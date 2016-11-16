package na.przypale.fitter.interactions

import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.{PostsRepository, SubscriptionsRepository}

class BrowsingPosts(postsRepository: PostsRepository, subscriptionsRepository: SubscriptionsRepository) {
  def browse(user: User): Unit = {
    println(subscriptionsRepository.findSubscriptionsOf(user.nick))
  }
}
