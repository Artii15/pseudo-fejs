package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Post
import na.przypale.fitter.repositories.PostsRepository

import scala.collection.JavaConverters

class CassandraPostsRepository(session: Session) extends PostsRepository {

  val incrementPostsCountStatement = session.prepare(
    "UPDATE users_counters SET number_of_created_posts = number_of_created_posts + 1 WHERE nick = :author")
  val insertPostStatement = session.prepare(
    "INSERT INTO posts(author, creation_time, content) VALUES(:author, :creationTime, :content)")
  override def create(post: Post): Unit = {
    val Post(author, creationTime, content) = post
    val incrementPostsCountQuery = incrementPostsCountStatement.bind()
      .setString("author", author)

    val insertPostQuery = insertPostStatement.bind()
      .setString("author", author)
      .setTimestamp("creationTIme", creationTime)
      .setString("content", content)

    session.execute(insertPostQuery)
    session.execute(incrementPostsCountQuery)
  }

  val findByAuthorStatement = session.prepare(
    "SELECT author, creation_time, content FROM posts WHERE author IN :authors")
  override def findByAuthors(authors: Iterable[String]): Iterable[Post] = {
    val query = findByAuthorStatement.bind()
      .setList("authors", JavaConverters.seqAsJavaList(authors.toSeq))

    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).toVector
      .map(row => Post(row.getString("author"), row.getTimestamp("creation_time"), row.getString("content")))
      .sortBy(_.creationTime)
  }
}
