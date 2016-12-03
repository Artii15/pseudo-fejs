package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.controls.{AnonymousUserControls, LoggedUserControls}
import na.przypale.fitter.interactions._
import na.przypale.fitter.repositories.cassandra._

object App {

  def start(session: Session): Unit = {
    val usersRepository = new CassandraUsersRepository(session)
    val postsRepository = new CassandraPostsRepository(session)
    val subscriptionsRepository = new CassandraSubscriptionsRepository(session)
    val commentsRepository = new CassandraCommentsRepository(session)
    val commentsCountersRepository = new CassandraCommentsCountersRepository(session)
    val postsCountersRepository = new CassandraPostsCountersRepository(session)
    val commentsLikesRepository = new CassandraCommentsLikesRepository(session)
    val postsLikesJournalRepository = new CassandraPostsLikesJournalRepository(session)

    val creatingUser = new CreatingUser(usersRepository, subscriptionsRepository)
    val loggingIn = new LoggingIn(usersRepository)
    val deletingUser = new DeletingUser(usersRepository)
    val creatingPost = new CreatingPost(postsRepository)
    val creatingComment = new CreatingComment(commentsRepository, commentsCountersRepository, postsCountersRepository)
    val likingUserContent = new LikingUserContent(commentsCountersRepository, postsCountersRepository, commentsLikesRepository, postsLikesJournalRepository)
    val subscribingUser = new SubscribingUser(usersRepository, subscriptionsRepository)
    val displayingPost = new DisplayingUserContent(commentsRepository, commentsCountersRepository, postsCountersRepository, creatingComment, likingUserContent)
    val browsingPosts = new BrowsingPosts(postsRepository, subscriptionsRepository, postsLikesJournalRepository, displayingPost)
    val searchingForUsers = new SearchingForUsers(usersRepository)

    val loggedUserControlsFactory = LoggedUserControls.makeFactory(creatingPost, deletingUser, subscribingUser,
      browsingPosts, searchingForUsers)

    new AnonymousUserControls(creatingUser, loggingIn, loggedUserControlsFactory).interact()
  }
}
