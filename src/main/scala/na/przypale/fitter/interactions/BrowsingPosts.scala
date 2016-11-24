package na.przypale.fitter.interactions

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.controls.PostsControls
import na.przypale.fitter.entities.{EnumeratedPost, Post, User, UserContent}
import na.przypale.fitter.menu.ActionIntId
import na.przypale.fitter.repositories.{PostsRepository, SubscriptionsRepository}

import scala.annotation.tailrec

class BrowsingPosts(postsRepository: PostsRepository,
                    subscriptionsRepository: SubscriptionsRepository,
                    displayingPost: DisplayingPost) {

  private val postsControls = new PostsControls()

  final def browse(user: User): Unit = {
    val subscribedPeople = subscriptionsRepository.findSubscriptionsOf(user.nick)
        .map(subscription => subscription.subscribedPersonNick)
    searchPosts(user, subscribedPeople)
  }

  @tailrec
  private def searchPosts(user: User, subscribedPeople: Iterable[String], lastDisplayedPost: Option[Post] = None) {
    val posts = postsRepository.findByAuthors(subscribedPeople, lastDisplayedPost)
    val enumeratedPosts = enumerate(posts)
    enumeratedPosts.foreach(display)

    if(posts.isEmpty) println("No more posts to display")
    else {
      postsControls.interact().id match {
        case ActionIntId(PostsControls.MORE_POSTS_ACTION_ID) => searchPosts(user, subscribedPeople, posts.lastOption)
        case ActionIntId(PostsControls.DISPLAY_POST_ACTION_ID) =>
          letUserSelectPost(user, enumeratedPosts)
          searchPosts(user, subscribedPeople, posts.lastOption)
        case _ =>
      }
    }
  }

  val postDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
  private def display(enumeratedPost: EnumeratedPost): Unit = {
    val EnumeratedPost(number, Post(author, timeId, content)) = enumeratedPost
    println(s"$number - ${postDateFormat.format(timeIdToDate(timeId))} $author:")
    println(content)
    println()
  }

  private def letUserSelectPost(user: User, posts: Iterable[EnumeratedPost]): Unit = {
    print("Post nr: ")
    val selectedPostNr = CommandLineReader.readInt()
    posts.find(post => post.number == selectedPostNr) match {
      case None =>
        println("Invalid post number")
        letUserSelectPost(user, posts)
      case Some(post) => displayingPost.display(user, post.post)
    }
  }

  private def enumerate(posts: Iterable[Post]): Iterable[EnumeratedPost] = posts.zip(Stream.from(1))
    .map{case (post, index) => EnumeratedPost(index, post) }

  private def timeIdToDate(timeId: UUID) = new Date((timeId.timestamp() - 0x01b21dd213814000L) / 10000)
}
