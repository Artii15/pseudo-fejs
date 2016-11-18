package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.{Post, Subscription, User}
import na.przypale.fitter.repositories.{PostsRepository, SubscriptionsRepository}

import scala.annotation.tailrec

class BrowsingPosts(postsRepository: PostsRepository, subscriptionsRepository: SubscriptionsRepository) {

  final def browse(user: User): Unit = {
    val subscriptions = subscriptionsRepository.findSubscriptionsOf(user.nick)
    val posts = findPosts(subscriptions)
    posts.foreach(display)

    if(posts.isEmpty) println("No posts to display")
    else showMenu(posts, subscriptions)
  }

  def findPosts(subscriptions: Iterable[Subscription], lastShownPost: Option[Post] = None) = {
    val subscribedPeopleNicks = subscriptions.map(subscription => subscription.subscribedPersonNick)
    postsRepository.findByAuthors(subscribedPeopleNicks, lastShownPost)
  }

  private def display(post: Post): Unit = {
    val Post(author, _, content) = post
    println(s"$author:")
    println(content)
  }

  @tailrec
  private def showMenu(posts: Iterable[Post], subscriptions: Iterable[Subscription]) {
    println("1 - More")
    println("2 - Read post")
    println("3 - Exit")

    CommandLineReader.readInt() match {
      case 1 => showMenu(findPosts(subscriptions, posts.lastOption), subscriptions)
      case 2 =>
      case 3 =>
      case _ => showMenu(posts, subscriptions)
    }
  }
}
