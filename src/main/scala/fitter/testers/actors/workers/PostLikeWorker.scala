package fitter.testers.actors.workers

import java.util.UUID

import fitter.entities.Credentials
import fitter.interactions.LikingUserContent
import fitter.logic.CreatingUser
import fitter.repositories.exceptions.{UserAlreadyExistsException, UserNotExistsException}
import fitter.testers.commands.posts.LikePost
import fitter.testers.results.posts.PostLiker

class PostLikeWorker(likingUserContent: LikingUserContent, creatingUser: CreatingUser)
  extends Worker[LikePost, PostLiker]{

  override protected def executeTask(task: LikePost): PostLiker = {
    try {
      val credentials = Credentials(task.nick, UUID.randomUUID().toString)
      creatingUser.create(credentials)
      likingUserContent.like(task.post, task.nick)
      PostLiker(Some(credentials))
    }
    catch {
      case _: UserNotExistsException | _: UserAlreadyExistsException =>
        PostLiker(None)
    }
  }
}
