package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.controls.{AnonymousUserControls, LoggedUserControls}
import na.przypale.fitter.repositories.cassandra.CassandraUsersRepository

object App {

  def start(session: Session): Unit = {
    val usersRepository = CassandraUsersRepository(session)
    val anonymousUserControls = AnonymousUserControls(usersRepository, LoggedUserControls.makeFactory(usersRepository))
    anonymousUserControls.interact()
  }
}
