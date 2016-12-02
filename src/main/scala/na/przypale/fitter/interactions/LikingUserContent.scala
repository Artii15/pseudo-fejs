package na.przypale.fitter.interactions

import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.entities.{Comment, Post, UserContent}
import na.przypale.fitter.repositories.{CommentsCountersRepository, CommentsLikesRepository, PostsCountersRepository}

class LikingUserContent(commentsCountersRepository: CommentsCountersRepository,
                        postsCountersRepository: PostsCountersRepository,
                        commentsLikesRepository: CommentsLikesRepository) {
  def like(userContent: UserContent, userName: String): Unit = {
    if(!checkIfAlreadyLiked(userContent, userName))
      userContent match {
        case comment: Comment =>
          commentsCountersRepository.likeComment(comment)
          commentsLikesRepository.create(comment, userName, UUIDs.timeBased())
        case post: Post =>
          postsCountersRepository.likePost(post)
        case _ =>
      }
  }

  private def checkIfAlreadyLiked(userContent: UserContent, userName: String): Boolean = {
    userContent match {
      case post: Post => false
      case comment: Comment => commentsLikesRepository.checkIfLikeExists(comment, userName)
    }
  }
}
