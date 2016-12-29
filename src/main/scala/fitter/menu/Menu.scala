package fitter.menu

import fitter.CommandLineReader

class Menu(val actions: Iterable[Action]) {
  def display(): Unit = actions.foreach(action => println(action.label))

  def read: Option[Action] = {
    val command = CommandLineReader.readString()
    actions.find(action => action.id matches command)
  }
}

object Menu {
  def apply(actions: Iterable[Action]) = new Menu(actions)
}
