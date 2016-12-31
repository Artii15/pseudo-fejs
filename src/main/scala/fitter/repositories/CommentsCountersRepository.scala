package fitter.repositories

import fitter.entities.Comment

trait CommentsCountersRepository {
  def getLikesAndAnswers(comment: Comment):  Map[String, Long]
  def likeComment(comment: Comment)
  def increaseAnswersCounter(comment: Comment)
}
