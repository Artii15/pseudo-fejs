package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Post
import na.przypale.fitter.repositories.PostsRepository

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
}

object CassandraPostsRepository {
  def apply(session: Session): CassandraPostsRepository = new CassandraPostsRepository(session)
}
