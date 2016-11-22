package na.przypale.fitter.controls
import na.przypale.fitter.menu.Action

abstract class PassiveControls extends Controls {
  override protected def handle(action: Action): Unit = Unit

  override protected def closesControls(action: Action): Boolean = true
}
