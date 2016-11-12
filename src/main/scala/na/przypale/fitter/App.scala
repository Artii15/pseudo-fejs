package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.logic.CreatingAccount
import na.przypale.fitter.menu.{Action, Menu}
import na.przypale.fitter.repositories.cassandra.CassandraUsersRepository

import scala.annotation.tailrec
import scala.collection.SortedMap

object App {
  private val EXIT_ACTION_ID = 2

  def start(session: Session): Unit = {
    val usersRepository = new CassandraUsersRepository(session)

    val menu = Menu(SortedMap(
      1 -> Action("Create user", () => {
        println("Enter user nick")
        CreatingAccount.create(usersRepository)(CommandLineReader.readString())
      }),
      EXIT_ACTION_ID -> Action("Exit", () => {})
    ))

    interactWithUser(menu)
  }

  @tailrec
  final def interactWithUser(menu: Menu): Unit = {
    menu.display()
    CommandLineReader.readInt() match {
      case EXIT_ACTION_ID =>
      case actionId => {
        menu.execute(actionId)
        interactWithUser(menu)
      }
    }
  }
}
