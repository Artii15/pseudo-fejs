package na.przypale.fitter.repositories

import na.przypale.fitter.entities.Post

trait PostsRepository {
  def create(post: Post)
  def findBySubscriber(subscriber: String)
}
