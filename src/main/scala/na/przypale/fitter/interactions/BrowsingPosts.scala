package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.controls.PostsControls
import na.przypale.fitter.entities.{EnumeratedPost, LikedPost, Post, User}
import na.przypale.fitter.menu.ActionIntId
import na.przypale.fitter.repositories.{PostsLikesJournalRepository, PostsRepository, SubscriptionsRepository}

import scala.annotation.tailrec

class BrowsingPosts(postsRepository: PostsRepository,
                    subscriptionsRepository: SubscriptionsRepository,
                    postsLikesJournalRepository: PostsLikesJournalRepository,
                    displayingPost: DisplayingUserContent) extends BrowsingUserContent{

  private val postsControls = new PostsControls()

  final def browse(user: User): Unit = {
    val subscribedPeople = subscriptionsRepository.findSubscriptionsOf(user.nick)
        .map(subscription => subscription.subscribedPersonNick)
    searchPosts(user, subscribedPeople)
  }

  def browseJournal(user: User): Unit = {
    searchLikedPosts(user)
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
          searchPosts(user, subscribedPeople, posts.headOption)
        case _ =>
      }
    }
  }

  @tailrec
  private def searchLikedPosts(user: User, lastDisplayedPost: Option[LikedPost] = None) {
    val likedPosts = postsLikesJournalRepository.getUserLikedPosts(user.nick, lastDisplayedPost)
    val posts = likedPosts.map(likedPost => postsRepository.getSpecificPost(likedPost.postAuthor, likedPost.postTimeId))
    val enumeratedPosts = enumerate(posts)
    enumeratedPosts.foreach(display)

    if(posts.isEmpty) println("No more posts to display")
    else {
      postsControls.interact().id match {
        case ActionIntId(PostsControls.MORE_POSTS_ACTION_ID) => searchLikedPosts(user, likedPosts.lastOption)
        case ActionIntId(PostsControls.DISPLAY_POST_ACTION_ID) =>
          letUserSelectPost(user, enumeratedPosts)
          searchLikedPosts(user)
        case _ =>
      }
    }
  }

  private def display(enumeratedPost: EnumeratedPost): Unit = {
    if (enumeratedPost.post != null) {
      val EnumeratedPost(number, Post(author, timeId, content)) = enumeratedPost
      println(s"$number - ${dateFormat.format(timeIdToDate(timeId))} $author:")
      println(content)
      println()
    }
  }

  @tailrec
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
}
