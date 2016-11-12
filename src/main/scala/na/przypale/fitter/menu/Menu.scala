package na.przypale.fitter.menu

import scala.collection.SortedMap

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
}

object Menu {
  def apply(actions: SortedMap[Int, Action]) = new Menu(actions)
}
