package fitter.repositories

import java.util.UUID

import fitter.entities.{LikedPost, Post}

trait PostsLikesJournalRepository {
  def create(post: Post, userName: String, timeId: UUID)
  def getUserLikedPosts(username: String, lastPostToSkip: Option[LikedPost] = None): Iterable[LikedPost]
  def checkIfLikeExists(post: Post, userName: String): Boolean
}
