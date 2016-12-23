package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.controls.{AnonymousUserControls, EventsControls, LoggedUserControls}
import na.przypale.fitter.interactions._
import na.przypale.fitter.logic.{Authenticating, CreatingPost, CreatingUser}
import na.przypale.fitter.repositories.cassandra._


object App {

  def start(session: Session): Unit = {
    lazy val usersRepository = new CassandraUsersRepository(session)
    lazy val postsRepository = new CassandraPostsRepository(session)
    lazy val subscriptionsRepository = new CassandraSubscriptionsRepository(session)
    lazy val eventsRepository = new CassandraEventsRepository(session)
    lazy val commentsRepository = new CassandraCommentsRepository(session)
    lazy val commentsCountersRepository = new CassandraCommentsCountersRepository(session)
    lazy val postsCountersRepository = new CassandraPostsCountersRepository(session)
    lazy val commentsLikesRepository = new CassandraCommentsLikesRepository(session)
    lazy val postsLikesJournalRepository = new CassandraPostsLikesJournalRepository(session)

    lazy val creatingUser = new CreatingUser(usersRepository, subscriptionsRepository)
    lazy val authenticating = new Authenticating(usersRepository)
    lazy val creatingPost = new CreatingPost(postsRepository)

    lazy val creatingUserUsingConsole = new CreatingUserUsingConsole(creatingUser)
    lazy val loggingIn = new LoggingIn(authenticating)
    lazy val deletingUser = new DeletingUser(usersRepository)
    lazy val creatingPostUsingConsole = new CreatingPostUsingConsole(creatingPost)
    lazy val creatingComment = new CreatingComment(commentsRepository, commentsCountersRepository, postsCountersRepository)
    lazy val likingUserContent = new LikingUserContent(commentsCountersRepository, postsCountersRepository, commentsLikesRepository, postsLikesJournalRepository)
    lazy val subscribingUser = new SubscribingUser(usersRepository, subscriptionsRepository)
    lazy val displayingPost = new DisplayingUserContent(commentsRepository, commentsCountersRepository, postsCountersRepository, creatingComment, likingUserContent)
    lazy val browsingPosts = new BrowsingPosts(postsRepository, subscriptionsRepository, postsLikesJournalRepository, displayingPost)
    lazy val searchingForUsers = new SearchingForUsers(usersRepository)
    lazy val joiningEvent = new JoiningEvent(eventsRepository)

    lazy val creatingEvent = new CreatingEvent(eventsRepository)
    lazy val browsingEvents = new BrowsingEvents(eventsRepository, joiningEvent)
    lazy val showingUserEvents = new ShowingUserEvents(eventsRepository)

    lazy val eventsControlsFactory = EventsControls.factory(creatingEvent, browsingEvents, showingUserEvents)
    lazy val loggedUserControlsFactory = LoggedUserControls.factory(creatingPostUsingConsole, deletingUser, subscribingUser,
      browsingPosts, searchingForUsers, eventsControlsFactory)

    new AnonymousUserControls(creatingUserUsingConsole, loggingIn, loggedUserControlsFactory).interact()
  }
}
