package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.controls.{AnonymousUserControls, LoggedUserControls}
import na.przypale.fitter.interactions.{CreatingPost, CreatingUser, LoggingIn}
import na.przypale.fitter.repositories.cassandra.{CassandraPostsRepository, CassandraUsersRepository}

object App {

  def start(session: Session): Unit = {
    val usersRepository = CassandraUsersRepository(session)
    val postsRepository = CassandraPostsRepository(session)

    val creatingUser = new CreatingUser(usersRepository)
    val loggingIn = new LoggingIn(usersRepository)
    val loggedUserControlsFactory = LoggedUserControls.makeFactory(new CreatingPost(postsRepository))

    val anonymousUserControls = AnonymousUserControls(creatingUser, loggingIn, loggedUserControlsFactory)

    anonymousUserControls.interact()
  }
}
