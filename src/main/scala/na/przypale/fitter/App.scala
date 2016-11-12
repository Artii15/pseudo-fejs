package na.przypale.fitter

import com.datastax.driver.core.Session
import na.przypale.fitter.menu.{Action, Menu}

import scala.annotation.tailrec
import scala.collection.SortedMap

object App {
  private val EXIT_ACTION_ID = 2

  def start(session: Session): Unit = {
    val menu = Menu(SortedMap(
      1 -> Action("Create user", () => {}),
      EXIT_ACTION_ID -> Action("Exit", () => {})
    ))

    interactWithUser(menu)
  }

  @tailrec
  final def interactWithUser(menu: Menu): Unit = {
    menu.display()
    menu.read() match {
      case EXIT_ACTION_ID =>
      case actionId => {
        menu.execute(actionId)
        interactWithUser(menu)
      }
    }
  }
}
