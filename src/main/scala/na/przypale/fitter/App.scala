package na.przypale.fitter

import na.przypale.fitter.controls.AnonymousUserControls


object App {

  def main(args: Array[String]): Unit = {
    Bootstrap.start(start)
  }

  def start(dependencies: Dependencies): Unit = {
    new AnonymousUserControls(dependencies.creatingUserUsingConsole,
      dependencies.loggingIn,
      dependencies.loggedUserControlsFactory).interact()
  }
}
