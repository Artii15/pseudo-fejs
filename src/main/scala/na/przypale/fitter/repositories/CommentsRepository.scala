package na.przypale.fitter.repositories

import na.przypale.fitter.entities.Comment

trait CommentsRepository {
  def create(comment: Comment)
}
