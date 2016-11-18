package na.przypale.fitter.interactions

import na.przypale.fitter.entities.{Post, User}
import na.przypale.fitter.repositories.{PostsRepository, SubscriptionsRepository}

class BrowsingPosts(postsRepository: PostsRepository, subscriptionsRepository: SubscriptionsRepository) {
  def browse(user: User): Unit = {
    val subscriptions = subscriptionsRepository.findSubscriptionsOf(user.nick)
    val subscribedPeopleNicks = subscriptions.map(subscription => subscription.subscribedPersonNick)
    val posts = postsRepository.findByAuthors(subscribedPeopleNicks)

    posts.foreach(displayPost)
    posts.isEmpty match {
      case true => println("No posts to display")
      case false => //TODO Let user select post to read or load more posts if available
    }
  }

  private def displayPost(post: Post): Unit = {
    val Post(author, _, content) = post
    println(s"$author:")
    println(content)
  }
}
