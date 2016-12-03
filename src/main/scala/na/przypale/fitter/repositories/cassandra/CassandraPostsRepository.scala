package na.przypale.fitter.repositories.cassandra

import java.util.UUID

import com.datastax.driver.core.Session
import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.Config
import na.przypale.fitter.entities.Post
import na.przypale.fitter.repositories.PostsRepository

import scala.collection.JavaConverters

class CassandraPostsRepository(session: Session) extends PostsRepository {

  private lazy val insertPostStatement = session.prepare(
    "INSERT INTO posts(author, time_id, content) " +
    "VALUES(:author, :timeId, :content)")
  override def create(post: Post): Unit = {
    val Post(author, timeId, content) = post

    val insertPostQuery = insertPostStatement.bind()
      .setString("author", author)
      .setUUID("timeId", timeId)
      .setString("content", content)

    session.execute(insertPostQuery)
  }

  private lazy val findByAuthorStatement = session.prepare(
    "SELECT author, time_id, content " +
    "FROM posts " +
    "WHERE author IN :authors AND time_id <= :timeId " +
    s"LIMIT ${Config.DEFAULT_PAGE_SIZE}"
  )
  override def findByAuthors(authors: Iterable[String], lastPostToSkip: Option[Post] = None): Iterable[Post] = {
    val postsAuthors = JavaConverters.seqAsJavaList(authors.toSeq)
    val timeId =getTimeId(lastPostToSkip)

    val query = findByAuthorStatement.bind()
      .setList("authors", postsAuthors)
      .setUUID("timeId", timeId)
    query.setFetchSize(Integer.MAX_VALUE)

    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).toVector
      .map(row => Post(row.getString("author"), row.getUUID("time_id"), row.getString("content")))
      .sortBy(post => post.timeId)
      .reverse
  }

  private lazy val getSpecificPostStatement = session.prepare(
    "SELECT author, time_id, content " +
      "FROM posts " +
      "WHERE author = :author AND time_id = :timeId"
  )

  override def getSpecificPost(postAuthor: String, postTimeId: UUID): Post = {
    val query = getSpecificPostStatement.bind()
      .setString("author", postAuthor)
      .setUUID("timeId", postTimeId)

    val row = session.execute(query).one()
    row match {
      case null => null
      case _ => Post(row.getString("author"), row.getUUID("time_id"), row.getString("content"))
    }
  }

  def getTimeId(likedPost: Option[Post]) = {
    likedPost match {
      case Some(post) => post.timeId
      case None =>  UUIDs.endOf(System.currentTimeMillis())
    }
  }
}
