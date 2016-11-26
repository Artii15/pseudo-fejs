package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.controls.{AnonymousUserControls, EventsControls, LoggedUserControls}
import na.przypale.fitter.interactions._
import na.przypale.fitter.repositories.cassandra.{CassandraEventsRepository, CassandraPostsRepository, CassandraSubscriptionsRepository, CassandraUsersRepository}

object App {

  def start(session: Session): Unit = {
    val usersRepository = new CassandraUsersRepository(session)
    val postsRepository = new CassandraPostsRepository(session)
    val subscriptionsRepository = new CassandraSubscriptionsRepository(session)
    val eventsRepository = new CassandraEventsRepository(session)

    val creatingUser = new CreatingUser(usersRepository, subscriptionsRepository)
    val loggingIn = new LoggingIn(usersRepository)
    val deletingUser = new DeletingUser(usersRepository)
    val creatingPost = new CreatingPost(postsRepository)
    val subscribingUser = new SubscribingUser(usersRepository, subscriptionsRepository)
    val displayingPost = new DisplayingPost()
    val browsingPosts = new BrowsingPosts(postsRepository, subscriptionsRepository, displayingPost)
    val searchingForUsers = new SearchingForUsers(usersRepository)

    val creatingEvent = new CreatingEvent(eventsRepository)
    val browsingEvents = new BrowsingEvents(eventsRepository)

    val eventsControlsFactory = EventsControls.factory(creatingEvent, browsingEvents)
    val loggedUserControlsFactory = LoggedUserControls.factory(creatingPost, deletingUser, subscribingUser,
      browsingPosts, searchingForUsers, eventsControlsFactory)

    new AnonymousUserControls(creatingUser, loggingIn, loggedUserControlsFactory).interact()
  }
}
