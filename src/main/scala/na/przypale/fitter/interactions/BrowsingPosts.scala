package na.przypale.fitter.interactions

import java.text.SimpleDateFormat

import na.przypale.fitter.controls.PostControls
import na.przypale.fitter.entities.{Post, User}
import na.przypale.fitter.menu.{Action, ActionIntId}
import na.przypale.fitter.repositories.{PostsRepository, SubscriptionsRepository}

import scala.annotation.tailrec

class BrowsingPosts(postsRepository: PostsRepository, subscriptionsRepository: SubscriptionsRepository) {

  private val postsControls = new PostControls()

  final def browse(user: User): Unit = {
    val subscribedPeople = subscriptionsRepository.findSubscriptionsOf(user.nick)
        .map(subscription => subscription.subscribedPersonNick)
    searchPosts(subscribedPeople)
  }

  @tailrec
  private def searchPosts(subscribedPeople: Iterable[String], lastDisplayedPost: Option[Post] = None) {
    val posts = postsRepository.findByAuthors(subscribedPeople, lastDisplayedPost)
    posts.foreach(display)

    if(posts.isEmpty || posts.size < postsRepository.NUMBER_OF_POSTS_PER_PAGE)
      println("No more posts to display")
    else {
      val Action(ActionIntId(actionId), _) = postsControls.interact()
      if(actionId == PostControls.MORE_POSTS_ACTION_ID) searchPosts(subscribedPeople, posts.lastOption)
    }
  }

  val postDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
  private def display(post: Post): Unit = {
    val Post(author, timeId, content) = post
    println(s"${postDateFormat.format(timeId)} $author:")
    println(content)
    println()
  }
}
