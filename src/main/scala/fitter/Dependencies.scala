package fitter

import com.datastax.driver.core.Session
import fitter.controls.{EventsControls, LoggedUserControls}
import fitter.entities.User
import fitter.interactions._
import fitter.logic.{Authenticating, CreatingEvent, CreatingPost, CreatingUser}
import fitter.repositories.cassandra._

class Dependencies(val session: Session) {
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
  lazy val creatingEvent = new CreatingEvent(eventsRepository)

  lazy val creatingUserUsingConsole = new CreatingUserUsingConsole(creatingUser)
  lazy val loggingIn = new LoggingIn(authenticating)
  lazy val deletingUser = new DeletingUser(usersRepository, eventsRepository)
  lazy val creatingPostUsingConsole = new CreatingPostUsingConsole(creatingPost)
  lazy val creatingComment = new CreatingComment(commentsRepository, commentsCountersRepository, postsCountersRepository)
  lazy val likingUserContent = new LikingUserContent(commentsCountersRepository, postsCountersRepository, commentsLikesRepository, postsLikesJournalRepository)
  lazy val subscribingUser = new SubscribingUser(usersRepository, subscriptionsRepository)
  lazy val displayingPost = new DisplayingUserContent(commentsRepository, commentsCountersRepository, postsCountersRepository, creatingComment, likingUserContent)
  lazy val browsingPosts = new BrowsingPosts(postsRepository, subscriptionsRepository, postsLikesJournalRepository, displayingPost)
  lazy val searchingForUsers = new SearchingForUsers(usersRepository)
  lazy val joiningEvent = new JoiningEvent(eventsRepository)

  lazy val creatingEventUsingConsole = new CreatingEventUsingConsole(creatingEvent)
  lazy val browsingEvents = new BrowsingEvents(eventsRepository, joiningEvent)
  lazy val showingUserEvents = new ShowingUserEvents(eventsRepository)

  lazy val eventsControlsFactory: (User) => EventsControls = EventsControls.factory(creatingEventUsingConsole, browsingEvents, showingUserEvents)
  lazy val loggedUserControlsFactory: (User) => LoggedUserControls = LoggedUserControls.factory(creatingPostUsingConsole, deletingUser, subscribingUser,
    browsingPosts, searchingForUsers, eventsControlsFactory)
}
