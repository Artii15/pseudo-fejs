package na.przypale.fitter.menu

class Menu(val actions: Vector[Action]) {
  def display(): Unit = {
    println("Choose action:")
    actions.foreach(action => println(s"${action.id} - ${action.label}"))
  }

  def execute(actionId: Int): Unit = {
    actions(actionId).operations()
  }
}
