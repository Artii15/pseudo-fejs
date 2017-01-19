package fitter.controls

import fitter.menu.{Action, Menu}

import scala.annotation.tailrec

abstract class Controls {

  @tailrec
  final def interact(): Action = {
    val menu = getMenu
    menu.display()
    val selectedAction = menu.read
    selectedAction match {
      case Some(action) =>
        handle(action)
        if (closesControls(action)) action else interact()
      case None => interact()
    }
  }

  protected def getMenu: Menu
  protected def handle(action: Action): Unit
  protected def closesControls(action: Action): Boolean
}
