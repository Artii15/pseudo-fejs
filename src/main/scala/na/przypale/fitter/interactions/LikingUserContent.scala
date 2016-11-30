package na.przypale.fitter.interactions

import na.przypale.fitter.entities.{Comment, Post, UserContent}
import na.przypale.fitter.repositories.{CommentsCountersRepository, PostsCountersRepository}

class LikingUserContent(commentsCountersRepository: CommentsCountersRepository, postsCountersRepository: PostsCountersRepository) {
  def like(userContent: UserContent): Unit = {
    userContent match {
      case comment: Comment =>
        commentsCountersRepository.likeComment(comment)
      case post: Post =>
        postsCountersRepository.likePost(post)
      case _ =>
    }
  }
}
