package na.przypale.fitter.repositories

import java.util.UUID

import na.przypale.fitter.entities.Post

trait PostsLikesJournalRepository {
  def create(post: Post, userName: String, timeId: UUID)
  def getAllUserLikedPosts(username: String, lastPostToSkip: Option[Post] = None): Iterable[Post]
  def checkIfLikeExists(post: Post, userName: String): Boolean
}
