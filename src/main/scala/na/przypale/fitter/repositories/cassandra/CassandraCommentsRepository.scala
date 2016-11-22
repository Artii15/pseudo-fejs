package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Comment
import na.przypale.fitter.repositories.CommentsRepository

class CassandraCommentsRepository(session: Session) extends CommentsRepository {

  lazy val insertCommentStatement = session.prepare(
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
}
