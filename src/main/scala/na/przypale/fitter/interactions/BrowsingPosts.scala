package na.przypale.fitter.interactions

import java.text.SimpleDateFormat

import na.przypale.fitter.controls.PostsControls
import na.przypale.fitter.entities.{Post, User}
import na.przypale.fitter.repositories.{PostsRepository, SubscriptionsRepository}

import scala.annotation.tailrec

class BrowsingPosts(postsRepository: PostsRepository, subscriptionsRepository: SubscriptionsRepository) {

  final def browse(user: User): Unit = {
    val subscribedPeople = subscriptionsRepository.findSubscriptionsOf(user.nick)
        .map(subscription => subscription.subscribedPersonNick)
    searchPosts(subscribedPeople)
  }

  @tailrec
  private def searchPosts(subscribedPeople: Iterable[String], lastDisplayedPost: Option[Post] = None) {
    val posts = postsRepository.findByAuthors(subscribedPeople, lastDisplayedPost)
    posts.foreach(display)

    if(posts.isEmpty) println("No more posts to display")
    else {
      new PostsControls(posts).interact()
      searchPosts(subscribedPeople, lastDisplayedPost)
    }
  }

  val postDateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss")
  private def display(post: Post): Unit = {
    val Post(author, creationTime, content) = post
    println(s"${postDateFormat.format(creationTime)} $author:")
    println(content)
  }
}
