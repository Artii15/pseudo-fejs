package fitter.repositories

import fitter.entities.{Comment, Post}

trait CommentsRepository {
  def create(comment: Comment)
  def findByPost(post: Post, lastCommentToSkip: Option[Comment] = None): Iterable[Comment]
  def findByParentComment(comment: Comment, lastCommentToSkip: Option[Comment] = None): Iterable[Comment]
}
