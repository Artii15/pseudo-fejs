package na.przypale.fitter.repositories

import java.util.UUID

import na.przypale.fitter.entities.Comment

trait CommentsLikesRepository {
  def create(comment: Comment, userName: String, timeId: UUID)
  def checkIfLikeExists(comment: Comment, userName: String): Boolean
}
