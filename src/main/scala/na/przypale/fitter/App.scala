package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.interactions.{CreatingUser, DeletingUser}
import na.przypale.fitter.menu.{Action, Menu}
import na.przypale.fitter.repositories.cassandra.CassandraUsersRepository

import scala.annotation.tailrec
import scala.collection.SortedMap

object App {

  def start(session: Session): Unit = {
    val usersRepository = new CassandraUsersRepository(session)
    val anonymousUserActions = SortedMap(
      1 -> Action("Create user", () => CreatingUser.create(usersRepository)),
      2 -> Action("Delete user", () => DeletingUser.delete(usersRepository)))

    val exitActionId = anonymousUserActions.size + 1
    val anonymousUserMenu = Menu(anonymousUserActions + (exitActionId -> Action("Exit", () => {})))

    interactWithAnonymousUser(anonymousUserMenu, exitActionId)
  }

  @tailrec
  final def interactWithAnonymousUser(menu: Menu, exitActionId: Int): Unit = {
    menu.display()
    CommandLineReader.readInt() match {
      case `exitActionId` =>
      case actionId => {
        menu.execute(actionId)
        interactWithAnonymousUser(menu, exitActionId)
      }
    }
  }
}
