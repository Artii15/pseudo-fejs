package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Post
import na.przypale.fitter.repositories.PostsCountersRepository

class CassandraPostsCountersRepository(session: Session) extends PostsCountersRepository{

  private lazy val getLikesAndCommentsStatement = session.prepare(
    "SELECT likes, comments " +
      "FROM posts_counters " +
      "WHERE author = :author AND time_id = :timeId"
  )

  override def getLikesAndComments(post: Post): Map[String, Long] = {

    val Post(author, timeId, _) = post

    val  getLikesAndCommentsQuery = getLikesAndCommentsStatement.bind()
      .setString("author", author)
      .setUUID("timeId", timeId)

    val row = session.execute(getLikesAndCommentsQuery).one()
    row match {
      case null => Map("Likes" -> 0, "Answers" -> 0)
      case _ => Map("Likes" -> row.getLong("likes"), "Comments" -> row.getLong("comments"))
    }
  }
  
  private lazy val likePostStatement = session.prepare(
    "UPDATE posts_counters " +
      "SET likes = likes + 1 " +
      "WHERE author = :author AND time_id = :timeId")

  override def likePost(post: Post): Unit = {
    val Post(author, timeId, _) = post

    val  likePostQuery = likePostStatement.bind()
      .setString("author", author)
      .setUUID("timeId", timeId)

    session.execute(likePostQuery)
  }

  private lazy val increaseCommentsStatement = session.prepare(
    "UPDATE posts_counters " +
      "SET comments = comments + 1 " +
      "WHERE author = :author AND time_id = :timeId")

  override def increaseCommentsCounter(post: Post): Unit = {
    val Post(author, timeId, _) = post

    val  increaseCommentsQuery = increaseCommentsStatement.bind()
      .setString("author", author)
      .setUUID("timeId", timeId)

    session.execute(increaseCommentsQuery)
  }
}
