package fitter.repositories.cassandra

import com.datastax.driver.core.Session
import fitter.entities.Comment
import fitter.repositories.CommentsCountersRepository

class CassandraCommentsCountersRepository(session: Session) extends CommentsCountersRepository{

  private lazy val getLikesAndAnswersStatement = session.prepare(
    "SELECT likes, answers " +
      "FROM comments_counters " +
      "WHERE post_author = :postAuthor AND post_time_id = :postTimeId AND comment_time_id = :commentTimeId  AND comment_author = :commentAuthor"
  )

  override def getLikesAndAnswers(comment: Comment): Map[String, Long] = {
    val Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, _, _, _) = comment

    val  getLikesAndAnswersQuery = getLikesAndAnswersStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setUUID("commentTimeId", commentTimeId)
      .setString("commentAuthor", commentAuthor)

    val row = session.execute(getLikesAndAnswersQuery).one()
    row match {
      case null => Map("Likes" -> 0, "Answers" -> 0)
      case _ => Map("Likes" -> row.getLong("likes"), "Answers" -> row.getLong("answers"))
    }
  }

  private lazy val likeCommentStatement = session.prepare(
    "UPDATE comments_counters " +
      "SET likes = likes + 1 " +
      "WHERE post_author = :postAuthor AND post_time_id = :postTimeId AND comment_time_id = :commentTimeId  AND comment_author = :commentAuthor")

  override def likeComment(comment: Comment): Unit = {
    val Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, _, _, _) = comment

    val likeCommentQuery = likeCommentStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setUUID("commentTimeId", commentTimeId)
      .setString("commentAuthor", commentAuthor)

    session.execute(likeCommentQuery)
  }

  private lazy val increaseAnswersStatement = session.prepare(
    "UPDATE comments_counters " +
      "SET answers = answers + 1 " +
      "WHERE post_author = :postAuthor AND post_time_id = :postTimeId AND comment_time_id = :commentTimeId  AND comment_author = :commentAuthor")

  override def increaseAnswersCounter(comment: Comment): Unit = {
    val Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, _, _, _) = comment

    val increaseAnswersQuery = increaseAnswersStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setUUID("commentTimeId", commentTimeId)
      .setString("commentAuthor", commentAuthor)

    session.execute(increaseAnswersQuery)
  }
}
