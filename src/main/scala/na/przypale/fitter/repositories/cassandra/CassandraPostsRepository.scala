package na.przypale.fitter.repositories.cassandra

import java.util.Calendar
import java.util.stream.{Collectors, IntStream}

import com.datastax.driver.core.Session
import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.Config
import na.przypale.fitter.entities.Post
import na.przypale.fitter.repositories.PostsRepository

import scala.collection.JavaConverters

class CassandraPostsRepository(session: Session) extends PostsRepository {

  lazy val insertPostStatement = session.prepare(
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

  lazy val findByAuthorStatement = session.prepare(
    "SELECT author, time_id, creation_time, content " +
    "FROM posts " +
    "WHERE author IN :authors AND year IN :years AND time_id < :timeId " +
    s"LIMIT $NUMBER_OF_POSTS_PER_PAGE"
  )
  override def findByAuthors(authors: Iterable[String], lastPostToSkip: Option[Post] = None) = {
    val postsAuthors = JavaConverters.seqAsJavaList(authors.toSeq)
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val yearsSinceAppCreation = IntStream.range(Config.applicationCreationYear, currentYear)
      .boxed().collect(Collectors.toList())
    val timeId = UUIDs.endOf(System.currentTimeMillis())

    val query = findByAuthorStatement.bind()
      .setList("authors", postsAuthors)
      .setList("years", yearsSinceAppCreation)
      .setUUID("timeId", timeId)
    query.setFetchSize(Integer.MAX_VALUE)

    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).toVector
      .map(row => Post(row.getString("author"), row.getTimestamp("creation_time"), row.getString("content")))
  }
}
