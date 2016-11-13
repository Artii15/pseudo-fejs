package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.controls.{AnonymousUserControls, LoggedUserControls}
import na.przypale.fitter.repositories.cassandra.CassandraUsersRepository

object App {

  def start(session: Session): Unit = {
    val usersRepository = new CassandraUsersRepository(session)
    val loggedUserControls = new LoggedUserControls()
    val anonymousUserControls = AnonymousUserControls(usersRepository)
    anonymousUserControls.interact()
  }
}
