package fitter.repositories

import java.util.UUID

import fitter.entities.Post

trait PostsRepository {
  def create(post: Post)
  def findByAuthors(authors: Iterable[String], lastPostToSkip: Option[Post] = None): Iterable[Post]
  def getSpecificPost(postAuthor: String, postTimeId: UUID): Post
}
