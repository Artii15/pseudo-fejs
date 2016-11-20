package na.przypale.fitter.repositories.cassandra

import java.util.Calendar

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Post
import na.przypale.fitter.repositories.PostsRepository

import scala.collection.JavaConverters

class CassandraPostsRepository(session: Session) extends PostsRepository {

  val insertPostStatement = session.prepare(
    "INSERT INTO posts(author, creation_year, time_id, creation_time, content) " +
    "VALUES(:author, :year, now(), :creationTime, :content)")
  override def create(post: Post): Unit = {
    val Post(author, creationTime, content) = post
    val calendar = Calendar.getInstance()
    calendar.setTime(creationTime)

    val insertPostQuery = insertPostStatement.bind()
      .setString("author", author)
      .setInt("year", calendar.get(Calendar.YEAR))
      .setTimestamp("creationTIme", creationTime)
      .setString("content", content)

    session.execute(insertPostQuery)
  }

  val findByAuthorNoPostSkipping = session.prepare(
    "SELECT author, creation_time, content " +
    "FROM posts " +
    "WHERE author IN :authors " +
    "ORDER BY creation_time DESC " +
    "LIMIT 10")
  val findByAuthorWithPostSkipping = session.prepare(
    "SELECT author, creation_time, content " +
    "FROM posts " +
    "WHERE author IN :authors AND creation_time < :creationTime " +
    "ORDER BY creation_time DESC " +
    "LIMIT 10")
  override def findByAuthors(authors: Iterable[String], lastPostToSkip: Option[Post] = None) = {
    val postsAuthors = JavaConverters.seqAsJavaList(authors.toSeq)
    val query = lastPostToSkip match {
      case None => findByAuthorNoPostSkipping.bind().setList("authors", postsAuthors)
      case Some(post) => findByAuthorWithPostSkipping.bind()
        .setList("authors", postsAuthors)
        .setTimestamp("creationTime", post.creationTime)
    }
    query.setFetchSize(Integer.MAX_VALUE)

    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).toVector
      .map(row => Post(row.getString("author"), row.getTimestamp("creation_time"), row.getString("content")))
  }
}
