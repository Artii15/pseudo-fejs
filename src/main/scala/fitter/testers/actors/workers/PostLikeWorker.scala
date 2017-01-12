package fitter.testers.actors.workers

import fitter.interactions.LikingUserContent
import fitter.logic.CreatingUser
import fitter.testers.commands.posts.LikePost
import fitter.testers.results.posts.PostLiker

class PostLikeWorker(likingUserContent: LikingUserContent, creatingUser: CreatingUser)
  extends Worker[LikePost, PostLiker]{

  override protected def executeTask(task: LikePost): PostLiker = {
    likingUserContent.like(task.post, task.nick)
    PostLiker(Some(task.nick))
  }
}
