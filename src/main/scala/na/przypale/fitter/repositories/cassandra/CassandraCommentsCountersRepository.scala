package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Comment
import na.przypale.fitter.repositories.CommentsCountersRepository

class CassandraCommentsCountersRepository(session: Session) extends CommentsCountersRepository{

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

    val likeCommentQuery = increaseAnswersStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setUUID("commentTimeId", commentTimeId)
      .setString("commentAuthor", commentAuthor)

    session.execute(likeCommentQuery)
  }
}
