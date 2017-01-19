package fitter.repositories.cassandra

import java.util.UUID

import com.datastax.driver.core.Session
import fitter.entities.{Comment, Post}
import fitter.repositories.CommentsRepository

import scala.collection.JavaConverters

class CassandraCommentsRepository(session: Session) extends CommentsRepository {

  private lazy val insertCommentStatement = session.prepare(
    "INSERT INTO comments(post_author, post_time_id, comment_time_id, comment_author, content, id, parent_id) " +
      "VALUES(:postAuthor, :postTimeId, :commentTimeId, :commentAuthor, :content, :id, :parentId)")

  override def create(comment: Comment): Unit = {
    val Comment(postAuthor, postTimeId, commentTimeId, commentAuthor, content, id, parentId) = comment

    val insertCommentQuery = insertCommentStatement.bind()
      .setString("postAuthor", postAuthor)
      .setUUID("postTimeId", postTimeId)
      .setUUID("commentTimeId", commentTimeId)
      .setString("commentAuthor", commentAuthor)
      .setString("content", content)
      .setUUID("id", id)

    val test = parentId match {
      case null => insertCommentQuery
      case _ => insertCommentQuery.setString("parentId", parentId)
    }

    session.execute(test)
  }

  private lazy val findByPostStatement = session.prepare(
    "SELECT post_author, post_time_id, comment_time_id, comment_author, content, id, parent_id " +
      "FROM comments " +
      "WHERE post_author = :postAuthor AND post_time_id = :postTimeId AND comment_time_id >= :timeId "
  )

  override def findByPost(post: Post, lastCommentToSkip: Option[Comment]): Iterable[Comment] = {
    val timeId = getTimeId(lastCommentToSkip)

    val query = findByPostStatement.bind()
      .setString("postAuthor", post.author)
      .setUUID("postTimeId", post.timeId)
      .setUUID("timeId", timeId)
    query.setFetchSize(Integer.MAX_VALUE)

    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).toVector
      .map(row => Comment(row.getString("post_author"), row.getUUID("post_time_id"), row.getUUID("comment_time_id"),
        row.getString("comment_author"), row.getString("content"), row.getUUID("id"), row.getString("parent_id")))
      .filter(comment => comment.parentId == "")
      .take(10)
  }

  private lazy val findByCommentStatement = session.prepare(
    "SELECT post_author, post_time_id, comment_time_id, comment_author, content, id, parent_id " +
      "FROM comments " +
      "WHERE post_author = :postAuthor AND post_time_id = :postTimeId AND comment_time_id >= :timeId"
  )

  override def findByParentComment(comment: Comment, lastCommentToSkip: Option[Comment]): Iterable[Comment] = {
    val timeId = getTimeId(lastCommentToSkip)

    val query = findByCommentStatement.bind()
      .setString("postAuthor", comment.postAuthor)
      .setUUID("postTimeId", comment.postTimeId)
      .setUUID("timeId", timeId)
    query.setFetchSize(Integer.MAX_VALUE)

    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).toVector
      .map(row => Comment(row.getString("post_author"), row.getUUID("post_time_id"), row.getUUID("comment_time_id"),
        row.getString("comment_author"), row.getString("content"), row.getUUID("id"), row.getString("parent_id")))
      .filter(c => c.parentId == comment.id.toString)
      .take(10)
  }

  def getTimeId(comment: Option[Comment]): UUID = {
    comment match {
      case Some(com) => com.commentTimeId
      case None => null
    }
  }
}
