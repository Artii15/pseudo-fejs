package na.przypale.fitter.interactions

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import na.przypale.fitter.Config
import na.przypale.fitter.controls.CommentsControls
import na.przypale.fitter.entities._
import na.przypale.fitter.menu.ActionIntId
import na.przypale.fitter.repositories.CommentsRepository

import scala.annotation.tailrec

class DisplayingPost(commentsRepository: CommentsRepository, creatingComment: CreatingComment) {

  val commentsControls = new CommentsControls

  def display(user: User, enumeratedPost: EnumeratedPost): Unit = {
    displayPost(enumeratedPost.post)
    searchComments(user, enumeratedPost.post)
  }

  @tailrec
  private def searchComments(user: User, post: Post, lastDisplayedComment: Option[Comment] = None) {
    val comments = commentsRepository.findByPost(post, lastDisplayedComment)
    val enumeratedComments = enumerate(comments)
    enumeratedComments.foreach(displayComment)

    if(comments.isEmpty || comments.size < Config.DEFAULT_PAGE_SIZE)
      println("No more comments to display")
    commentsControls.interact().id match {
      case ActionIntId(CommentsControls.MORE_COMMENTS_ACTION_ID) => searchComments(user, post, comments.lastOption)
      case ActionIntId(CommentsControls.CREATE_COMMENT_ACTION_ID) => creatingComment.create(user, post)
      case _ =>
    }
  }

  val dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
  private def displayComment(enumeratedComment: EnumeratedComment): Unit = {
    val EnumeratedComment(number, Comment(_, _, commentTimeId, commentAuthor, content, _, _)) = enumeratedComment
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
