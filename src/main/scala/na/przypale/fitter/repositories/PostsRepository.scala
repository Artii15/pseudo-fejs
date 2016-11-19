package na.przypale.fitter.repositories

import na.przypale.fitter.entities.Post

trait PostsRepository {
  val NUMBER_OF_POSTS_PER_PAGE = 10

  def create(post: Post)
  def findByAuthors(authors: Iterable[String], lastPostToSkip: Option[Post] = None): Iterable[Post]
}
