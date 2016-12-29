package fitter.controls
import fitter.menu.Action

abstract class PassiveControls extends Controls {
  override protected def handle(action: Action): Unit = Unit

  override protected def closesControls(action: Action): Boolean = true
}
