package na.przypale.fitter.repositories

import na.przypale.fitter.entities.Comment

trait CommentsCountersRepository {
  def likeComment(comment: Comment)
  def increaseAnswersCounter(comment: Comment)
}
