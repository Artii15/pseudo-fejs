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

  override def getLikesAndComments(post: Post): Map[String, Int] = {

    val Post(author, timeId, _) = post

    val  getLikesAndCommentsQuery = getLikesAndCommentsStatement.bind()
      .setString("author", author)
      .setUUID("timeId", timeId)

    val row = session.execute(getLikesAndCommentsQuery).one()
    println(row)
    Map("Likes" -> row.getInt("likes"), "Answers" -> row.getInt("answers"))
  }

  private lazy val initiateCountersStatement = session.prepare(
    "INSERT INTO posts_counters(author, time_id, likes, comments) " +
      "VALUES(:author, :timeId, 0, 0)"
  )

  override def initiateCounters(post: Post): Unit = {
    val Post(author, timeId, _) = post

    val  initiateCountersQuery = initiateCountersStatement.bind()
      .setString("author", author)
      .setUUID("timeId", timeId)

    session.execute(initiateCountersQuery)
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
