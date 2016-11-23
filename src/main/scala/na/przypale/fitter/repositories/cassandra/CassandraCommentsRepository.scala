package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.Config
import na.przypale.fitter.entities.{Comment, Post}
import na.przypale.fitter.repositories.CommentsRepository

import scala.collection.JavaConverters

class CassandraCommentsRepository(session: Session) extends CommentsRepository {

  private lazy val insertCommentStatement = session.prepare(
    "INSERT INTO comments(post_author, post_time_id, comment_time_id, comment_author, content, id, parent_id) " +
      "VALUES(:postAuthor, :postTimeId, :commentTimeId, :commentAuthor, :content, :id, :parentId)")

  override def create(comment: Comment): Unit = {
    //val Post(postAuthor, postTimeId, postContent) = post
    val Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, content, id, parentId) = comment

    val insertCommentQuery = insertCommentStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setUUID("commentTimeId", commentTimeId)
      .setString("commentAuthor", commentAuthor)
      .setString("content", content)
      .setUUID("id", id)
      .setUUID("parentId", parentId)

    session.execute(insertCommentQuery)
  }

  private lazy val findByPostStatement = session.prepare(
    "SELECT post_author, post_time_id, comment_time_id, comment_author, content, id, parent_id " +
      "FROM comments " +
      "WHERE post_author = :postAuthor AND post_time_id = :postTimeId AND comment_time_id > :timeId " +
      s"LIMIT ${Config.DEFAULT_PAGE_SIZE}"
  )

  override def findByPost(post: Post, lastCommentToSkip: Option[Comment]): Iterable[Comment] = {
    val timeId = UUIDs.endOf(System.currentTimeMillis())

    val query = findByPostStatement.bind()
      .setString("postAuthor", post.author)
      .setUUID("post_time_id", post.timeId)
      .setUUID("timeId", timeId)
    query.setFetchSize(Integer.MAX_VALUE)

    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).toVector
      .map(row => Comment(row.getString("post_author"), row.getUUID("post_time_id"), row.getUUID("comment_time_id"),
        row.getString("comment_author"), row.getString("content"), row.getUUID("id"), row.getUUID("parent_id")))
  }
}
