package fitter.repositories.cassandra

import java.util.UUID

import com.datastax.driver.core.Session
import com.datastax.driver.core.utils.UUIDs
import fitter.Config
import fitter.entities.{LikedPost, Post}
import fitter.repositories.PostsLikesJournalRepository

import scala.collection.JavaConverters

class CassandraPostsLikesJournalRepository(session: Session) extends PostsLikesJournalRepository{

  override def create(post: Post, userName: String, timeId: UUID): Unit = {
    createPostLike(post, userName, timeId)
    createUserLike(post, userName, timeId)
  }

  private lazy val insertPostLikeStatement = session.prepare(
    "INSERT INTO posts_likes_journal(post_author, post_time_id, author, time_id) " +
      "VALUES(:postAuthor, :postTimeId, :author, :timeId)"
  )
  def createPostLike(post: Post, userName: String, timeId: UUID): Unit = {
    val Post(postAuthor, postTimeId, _) = post

    val insertPostLikeQuery = insertPostLikeStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setString("author", userName)
      .setUUID("timeId", timeId)

    session.execute(insertPostLikeQuery)
  }

  private lazy val insertUserLikeStatement = session.prepare(
    "INSERT INTO users_likes_journal(author, time_id, post_author, post_time_id) " +
      "VALUES(:author, :timeId, :postAuthor, :postTimeId)"
  )
  private def createUserLike(post: Post, userName: String, timeId: UUID): Unit = {
    val Post(postAuthor, postTimeId, _) = post

    val insertPostLikeQuery = insertUserLikeStatement.bind()
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

  private lazy val findUserLikedPostsStatement = session.prepare(
  "SELECT author, time_id, post_author, post_time_id " +
    "FROM users_likes_journal " +
    "WHERE author = :author AND time_id < :timeId " +
    s"LIMIT ${Config.DEFAULT_PAGE_SIZE}"
)

  override def getUserLikedPosts(username: String, lastPostToSkip: Option[LikedPost] = None): Iterable[LikedPost] = {
    val timeId = getTimeId(lastPostToSkip)

    val query = findUserLikedPostsStatement.bind()
      .setString("author", username)
      .setUUID("timeId", timeId)
    query.setFetchSize(Integer.MAX_VALUE)

    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).toVector
      .map(row => LikedPost(row.getString("post_author"), row.getUUID("post_time_id"), row.getUUID("time_id")))
      .sortBy(likedPost => likedPost.likeTimeId)
      .reverse
  }

  def getTimeId(likedPost: Option[LikedPost]) = {
    likedPost match {
      case Some(post) => post.likeTimeId
      case None =>  UUIDs.endOf(System.currentTimeMillis())
    }
  }
}
