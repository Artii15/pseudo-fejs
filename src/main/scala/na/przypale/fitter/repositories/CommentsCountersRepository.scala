package na.przypale.fitter.repositories

import na.przypale.fitter.entities.Comment

trait CommentsCountersRepository {
  def getLikesAndAnswers(comment: Comment):  Map[String, Int]
  def likeComment(comment: Comment)
  def increaseAnswersCounter(comment: Comment)
  def initiateCounters(comment: Comment)
}
