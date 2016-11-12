package na.przypale.fitter.menu

import scala.annotation.tailrec
import scala.collection.SortedMap
import scala.io.StdIn

class Menu(val actions: SortedMap[Int, Action]) {
  def display(): Unit = {
    println("Choose action:")
    actions.foreach { case (id, action) => println(s"$id - ${action.label}") }
  }

  def execute(actionId: Int): Unit = {
    if(actions.contains(actionId)) {
      actions(actionId).operations()
    }
  }

  @tailrec
  final def read(): Int = {
    try {
      val selectedCommand = StdIn.readLine()
      selectedCommand.toInt
    }
    catch {
      case _: Throwable => read()
    }
  }
}

object Menu {
  def apply(actions: SortedMap[Int, Action]) = new Menu(actions)
}
