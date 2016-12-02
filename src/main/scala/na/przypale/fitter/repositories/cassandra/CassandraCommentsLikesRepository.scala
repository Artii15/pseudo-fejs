package na.przypale.fitter.repositories.cassandra

import java.util.UUID

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Comment
import na.przypale.fitter.repositories.CommentsLikesRepository

class CassandraCommentsLikesRepository(session: Session) extends CommentsLikesRepository{

  private lazy val insertCommentLikeStatement = session.prepare(
    "INSERT INTO comments_likes(post_author, post_time_id, comment_time_id, comment_author, author, time_id) " +
      "VALUES(:postAuthor, :postTimeId, :commentTimeId, :commentAuthor, :author, :timeId)"
  )

  override def create(comment: Comment, userName: String, timeId: UUID): Unit = {
    val Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, _, _, _) = comment

    val insertCommentLikeQuery = insertCommentLikeStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setUUID("commentTimeId", commentTimeId)
      .setString("commentAuthor", commentAuthor)
      .setString("author", userName)
      .setUUID("timeId", timeId)

    session.execute(insertCommentLikeQuery)
  }

  private lazy val getCommentLikeStatement = session.prepare(
    "SELECT post_author, post_time_id, comment_time_id, comment_author, author, time_id " +
      "FROM comments_likes " +
      "WHERE post_author = :postAuthor AND post_time_id = :postTimeId AND comment_time_id = :commentTimeId AND comment_author = :commentAuthor AND author = :author"
  )

  override def checkIfLikeExists(comment: Comment, userName: String): Boolean = {
    val Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, _, _, _) = comment

    val getCommentLikeQuery = getCommentLikeStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setUUID("commentTimeId", commentTimeId)
      .setString("commentAuthor", commentAuthor)
      .setString("author", userName)

    val row = session.execute(getCommentLikeQuery).one()
    row match {
      case null => false
      case _ => true
    }
  }
}
