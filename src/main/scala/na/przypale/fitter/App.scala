package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.controls.{AnonymousUserControls, LoggedUserControls}
import na.przypale.fitter.repositories.cassandra.CassandraUsersRepository

object App {

  def start(session: Session): Unit = {
    val usersRepository = CassandraUsersRepository(session)
    val loggedUserControls = LoggedUserControls()
    val anonymousUserControls = AnonymousUserControls(usersRepository, loggedUserControls)
    anonymousUserControls.interact()
  }
}
