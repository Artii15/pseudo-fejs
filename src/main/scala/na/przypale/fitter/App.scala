package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.controls.{AnonymousUserControls, LoggedUserControls}
import na.przypale.fitter.interactions._
import na.przypale.fitter.repositories.cassandra.{CassandraPostsRepository, CassandraSubscriptionsRepository, CassandraUsersRepository}

object App {

  def start(session: Session): Unit = {
    val usersRepository = new CassandraUsersRepository(session)
    val postsRepository = new CassandraPostsRepository(session)
    val subscriptionsRepository = new CassandraSubscriptionsRepository(session)

    val creatingUser = new CreatingUser(usersRepository)
    val loggingIn = new LoggingIn(usersRepository)
    val deletingUser = new DeletingUser(usersRepository)
    val creatingPost = new CreatingPost(postsRepository)
    val subscribingUser = new SubscribingUser(usersRepository, subscriptionsRepository)

    val loggedUserControlsFactory = LoggedUserControls.makeFactory(creatingPost, deletingUser, subscribingUser)

    new AnonymousUserControls(creatingUser, loggingIn, loggedUserControlsFactory).interact()
  }
}
