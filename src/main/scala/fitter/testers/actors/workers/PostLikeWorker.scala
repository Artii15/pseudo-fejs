package fitter.testers.actors.workers

import com.datastax.driver.core.exceptions.NoHostAvailableException
import fitter.interactions.LikingUserContent
import fitter.testers.commands.posts.LikePost
import fitter.testers.results.posts.PostLiker

class PostLikeWorker(likingUserContent: LikingUserContent)
  extends Worker[LikePost, PostLiker]{

  override protected def executeTask(task: LikePost): PostLiker = {
    try {
      likingUserContent.like(task.post, task.nick)
      PostLiker(Some(task.nick))
    }
    catch {
      case _: NoHostAvailableException => PostLiker(None)
    }
  }
}
