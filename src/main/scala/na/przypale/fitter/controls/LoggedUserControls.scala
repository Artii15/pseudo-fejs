package na.przypale.fitter.controls

import na.przypale.fitter.entities.User

class LoggedUserControls {
  def interact(user: User): Unit = {

  }
}

object LoggedUserControls {
  def apply(): LoggedUserControls = new LoggedUserControls()
}
