package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.controls.{AnonymousUserControls}
import na.przypale.fitter.repositories.cassandra.CassandraUsersRepository

object App {

  def start(session: Session): Unit = {
    val usersRepository = CassandraUsersRepository(session)
    val anonymousUserControls = AnonymousUserControls(usersRepository)
    anonymousUserControls.interact()
  }
}
