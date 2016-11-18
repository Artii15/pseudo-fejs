package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Post
import na.przypale.fitter.repositories.PostsRepository

import scala.collection.JavaConverters

class CassandraPostsRepository(session: Session) extends PostsRepository {

  val insertPostStatement = session.prepare(
    "INSERT INTO posts(author, creation_time, content) VALUES(:author, :creationTime, :content)")
  override def create(post: Post): Unit = {
    val Post(author, creationTime, content) = post

    val insertPostQuery = insertPostStatement.bind()
      .setString("author", author)
      .setTimestamp("creationTIme", creationTime)
      .setString("content", content)

    session.execute(insertPostQuery)
  }

  val findByAuthorStatement = session.prepare(
    "SELECT author, creation_time, content FROM posts WHERE author IN :authors")
  override def findByAuthors(authors: Iterable[String]): Iterable[Post] = {
    val postsAuthors = JavaConverters.seqAsJavaList(authors.toSeq)
    val findPostsQuery = findByAuthorStatement.bind()
      .setList("authors", postsAuthors)

    JavaConverters.collectionAsScalaIterable(session.execute(findPostsQuery).all()).toVector
      .map(row => Post(row.getString("author"), row.getTimestamp("creation_time"), row.getString("content")))
      .sortBy(_.creationTime)
  }
}
