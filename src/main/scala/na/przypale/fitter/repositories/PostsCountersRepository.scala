package na.przypale.fitter.repositories

import na.przypale.fitter.entities.Post

trait PostsCountersRepository {
  def getLikesAndComments(post: Post):  Map[String, Long]
  def likePost(post: Post)
  def increaseCommentsCounter(post: Post)
}
