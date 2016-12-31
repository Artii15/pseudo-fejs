package fitter.controls
import fitter.menu.{Action, Menu}

class PostControls extends Controls {
  override protected def getMenu: Menu = ???

  override protected def handle(action: Action): Unit = ???

  override protected def closesControls(action: Action): Boolean = ???
}
