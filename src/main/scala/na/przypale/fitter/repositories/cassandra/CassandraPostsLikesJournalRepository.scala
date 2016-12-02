package na.przypale.fitter.repositories.cassandra

import java.util.UUID

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Post
import na.przypale.fitter.repositories.PostsLikesJournalRepository

class CassandraPostsLikesJournalRepository(session: Session) extends PostsLikesJournalRepository{

  private lazy val insertPostLikeStatement = session.prepare(
    "INSERT INTO posts_likes_journal(post_author, post_time_id, author, time_id) " +
      "VALUES(:postAuthor, :postTimeId, :author, :timeId)"
  )
  override def create(post: Post, userName: String, timeId: UUID): Unit = {
    val Post(postAuthor, postTimeId, _) = post

    val insertPostLikeQuery = insertPostLikeStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setString("author", userName)
      .setUUID("timeId", timeId)

    session.execute(insertPostLikeQuery)
  }

  private lazy val getPostLikeStatement = session.prepare(
    "SELECT post_author, post_time_id, author, time_id " +
      "FROM posts_likes_journal " +
      "WHERE post_author = :postAuthor AND post_time_id = :postTimeId AND author = :author"
  )

  override def checkIfLikeExists(post: Post, userName: String): Boolean = {
    val Post(postAuthor, postTimeId, _) = post

    val getPostLikeQuery = getPostLikeStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setString("author", userName)

    val row = session.execute(getPostLikeQuery).one()
    row match {
      case null => false
      case _ => true
    }
  }

  override def getAllUserLikedPosts(username: String, lastPostToSkip: Option[Post]): Iterable[Post] = ???
}
