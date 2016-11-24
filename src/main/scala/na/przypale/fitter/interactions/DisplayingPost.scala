package na.przypale.fitter.interactions

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import na.przypale.fitter.Config
import na.przypale.fitter.controls.CommentsControls
import na.przypale.fitter.entities.{Comment, EnumeratedComment, EnumeratedPost, Post}
import na.przypale.fitter.menu.{Action, ActionIntId}
import na.przypale.fitter.repositories.CommentsRepository

import scala.annotation.tailrec

class DisplayingPost(commentsRepository: CommentsRepository) {
  def display(enumeratedPost: EnumeratedPost): Unit = {
    //println(s"Displayed post content:\n ${enumeratedPost.post.content}")
    displayPost(enumeratedPost.post)
    searchComments(enumeratedPost.post)
  }

  //@tailrec
  private def searchComments(post: Post, lastDisplayedComment: Option[Comment] = None) {
    val comments = commentsRepository.findByPost(post, lastDisplayedComment)
    val enumeratedComments = enumerate(comments)
    //displayPost(post)
    enumeratedComments.foreach(displayComment)

    if(comments.isEmpty || comments.size < Config.DEFAULT_PAGE_SIZE)
      println("No more comments to display")
    //val Action(ActionIntId(actionId), _) = commentsControls.interact()
    //if(actionId == CommentsControls.MORE_COMMENTS_ACTION_ID) searchComments(post, comments.lastOption)
  }

  val dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
  private def displayComment(enumeratedComment: EnumeratedComment): Unit = {
    val EnumeratedComment(number, Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, content, id, parentId)) = enumeratedComment
    println(s"\t$number - ${dateFormat.format(timeIdToDate(commentTimeId))} $commentAuthor:")
    println("\t" + content)
    println()
  }

  private def displayPost(post: Post): Unit = {
    val Post(author, timeId, content) = post
    println(s"${dateFormat.format(timeIdToDate(timeId))} $author:")
    println(content)
    println()
  }

  private def enumerate(comments: Iterable[Comment]): Iterable[EnumeratedComment] = comments.zip(Stream.from(1))
    .map{case (comment, index) => EnumeratedComment(index, comment) }

  private def timeIdToDate(timeId: UUID) = new Date((timeId.timestamp() - 0x01b21dd213814000L) / 10000)
}
