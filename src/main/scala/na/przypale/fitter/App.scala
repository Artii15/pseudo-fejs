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
    val actions = SortedMap(
      1 -> Action("Create user", () => CreatingUser.create(usersRepository)),
      2 -> Action("Delete user", () => DeletingUser.delete(usersRepository)))

    val exitActionId = actions.size + 1
    val menu = Menu(actions + (exitActionId -> Action("Exit", () => {})))

    interactWithUser(menu, exitActionId)
  }

  @tailrec
  final def interactWithUser(menu: Menu, exitActionId: Int): Unit = {
    menu.display()
    CommandLineReader.readInt() match {
      case `exitActionId` =>
      case actionId => {
        menu.execute(actionId)
        interactWithUser(menu, exitActionId)
      }
    }
  }
}
