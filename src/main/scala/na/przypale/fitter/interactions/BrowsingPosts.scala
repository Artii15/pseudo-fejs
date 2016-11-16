package na.przypale.fitter.interactions

import na.przypale.fitter.entities.{Post, Subscription, User}
import na.przypale.fitter.repositories.{PostsRepository, SubscriptionsRepository}

class BrowsingPosts(postsRepository: PostsRepository, subscriptionsRepository: SubscriptionsRepository) {
  def browse(user: User): Unit = {
    val subscriptions = subscriptionsRepository.findSubscriptionsOf(user.nick)
    subscriptions.foreach(displayPosts)
  }

  private def displayPosts(subscription: Subscription): Unit = {
    postsRepository.findByAuthor(subscription.subscribedPersonNick).foreach(post => {
      val Post(author, _, content) = post
      println(s"$author:")
      println(content)
    })
  }
}
