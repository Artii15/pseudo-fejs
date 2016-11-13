package na.przypale.fitter.controls

import na.przypale.fitter.menu.{Action, Menu}

import scala.annotation.tailrec

abstract class Controls {

  @tailrec
  final def interact(): Unit = {
    val menu = getMenu
    menu.display()
    menu.read match {
      case Some(action) =>
        handle(action)
        if (!closesControls(action)) interact()
      case _ => interact()
    }
  }

  protected abstract def getMenu: Menu
  protected abstract def handle(action: Action): Unit
  protected abstract def closesControls(action: Action): Boolean
}
