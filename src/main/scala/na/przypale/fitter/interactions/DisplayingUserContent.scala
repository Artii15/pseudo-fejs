package na.przypale.fitter.interactions

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import na.przypale.fitter.{CommandLineReader, Config}
import na.przypale.fitter.controls.UserContentControls
import na.przypale.fitter.entities._
import na.przypale.fitter.menu.ActionIntId
import na.przypale.fitter.repositories.CommentsRepository

import scala.annotation.tailrec

class DisplayingUserContent(commentsRepository: CommentsRepository, creatingComment: CreatingComment) {

  val userContentControls = new UserContentControls

  def display(user: User, userContent: UserContent): Unit = {
    searchComments(user, userContent)
  }

  @tailrec
  private def searchComments(user: User, userContent: UserContent, lastDisplayedComment: Option[Comment] = None) {

    val comments = userContent match {
      case post: Post => commentsRepository.findByPost(post, lastDisplayedComment)
      case comment: Comment => commentsRepository.findByParentComment(comment, lastDisplayedComment)
    }

    val enumeratedComments = enumerate(comments)
    displayUserContent(userContent)
    enumeratedComments.foreach(displayComment)

    if(comments.isEmpty || comments.size < Config.DEFAULT_PAGE_SIZE)
      println("No more comments to display")
    userContentControls.interact().id match {
      case ActionIntId(UserContentControls.MORE_COMMENTS_ACTION_ID) =>
        searchComments(user, userContent, comments.lastOption)
      case ActionIntId(UserContentControls.CREATE_COMMENT_ACTION_ID) =>
        creatingComment.create(user, userContent)
        searchComments(user, userContent)
      case ActionIntId(UserContentControls.DISPLAY_COMMENT_ACTION_ID) =>
        letUserSelectComment(user, enumeratedComments)
        searchComments(user, userContent)
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

  private def displayUserContent(userContent: UserContent): Unit = {
    userContent match {
      case post: Post =>
        println(s"${dateFormat.format(timeIdToDate(post.timeId))} ${post.author}:")
        println(post.content)
      case comment: Comment =>
        println(s"${dateFormat.format(timeIdToDate(comment.commentTimeId))} ${comment.commentAuthor}:")
        println(comment.content)
    }
    println()
  }

  private def enumerate(comments: Iterable[Comment]): Iterable[EnumeratedComment] = comments.zip(Stream.from(1))
    .map{case (comment, index) => EnumeratedComment(index, comment) }

  private def timeIdToDate(timeId: UUID) = new Date((timeId.timestamp() - 0x01b21dd213814000L) / 10000)

  @tailrec
  private def letUserSelectComment(user: User, comments: Iterable[EnumeratedComment]): Unit = {
    if (comments.isEmpty)
      println("No comments to display")
    else {
      print("Comment nr: ")
      val selectedPostNr = CommandLineReader.readInt()
      comments.find(post => post.number == selectedPostNr) match {
        case None =>
          println("Invalid comment number")
          letUserSelectComment(user, comments)
        case Some(comment) => display(user, comment.comment)
      }
    }
  }
}
