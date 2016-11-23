package na.przypale.fitter.interactions

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import na.przypale.fitter.Config
import na.przypale.fitter.controls.CommentsControls
import na.przypale.fitter.entities.{Post, Comment}
import na.przypale.fitter.menu.{Action, ActionIntId}
import na.przypale.fitter.repositories.CommentsRepository

import scala.annotation.tailrec

class BrowsingComments(commentsRepository: CommentsRepository) {
  private val commentsControls = new CommentsControls()

  final def browse(post: Post): Unit = {
    searchComments(post)
  }

  @tailrec
  private def searchComments(post: Post, lastDisplayedComment: Option[Comment] = None) {
    val comments = commentsRepository.findByPost(post, lastDisplayedComment)
    displayPost(post)
    comments.foreach(displayComment)

    if(comments.isEmpty || comments.size < Config.DEFAULT_PAGE_SIZE)
      println("No more comments to display")
    val Action(ActionIntId(actionId), _) = commentsControls.interact()
    if(actionId == CommentsControls.MORE_COMMENTS_ACTION_ID) searchComments(post, comments.lastOption)
  }

  val dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
  private def displayComment(comment: Comment): Unit = {
    val Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, content, id, parentId) = comment
    println(s"\t${dateFormat.format(timeIdToDate(commentTimeId))} $commentAuthor:")
    println("\t" + content)
    println()
  }

  private def displayPost(post: Post): Unit = {
    val Post(author, timeId, content) = post
    println(s"${dateFormat.format(timeIdToDate(timeId))} $author:")
    println(content)
    println()
  }

  private def timeIdToDate(timeId: UUID) = new Date((timeId.timestamp() - 0x01b21dd213814000L) / 10000)

}
