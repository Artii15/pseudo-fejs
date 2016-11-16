package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.{Row, Session}
import na.przypale.fitter.entities.Post
import na.przypale.fitter.repositories.PostsRepository

import scala.collection.JavaConverters

class CassandraPostsRepository(session: Session) extends PostsRepository {

  val insertPostStatement = session.prepare(
    "INSERT INTO posts(author, creation_time, content) VALUES(:author, :creationTime, :content)")
  override def create(post: Post): Unit = {
    val query = insertPostStatement.bind()
      .setString("author", post.author)
      .setTimestamp("creationTIme", post.creationTime)
      .setString("content", post.content)

    session.execute(query)
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
