package na.przypale.fitter.interactions

import na.przypale.fitter.entities.{Post, User}
import na.przypale.fitter.repositories.{PostsRepository, SubscriptionsRepository}

class BrowsingPosts(postsRepository: PostsRepository, subscriptionsRepository: SubscriptionsRepository) {
  def browse(user: User): Unit = {
    val subscriptions = subscriptionsRepository.findSubscriptionsOf(user.nick)
    postsRepository.findByAuthors(subscriptions.map(subscription => subscription.subscribedPersonNick))
        .foreach(displayPost)
  }

  private def displayPost(post: Post): Unit = {
    val Post(author, _, content) = post
    println(s"$author:")
    println(content)
  }
}
