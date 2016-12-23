package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.{Credentials, User}
import na.przypale.fitter.logic.Authenticating
import na.przypale.fitter.logic.exceptions.AuthenticationException

class LoggingIn(authenticating: Authenticating) {

  def logIn(): Option[User] = {
    print("Nick: ")
    val nick = CommandLineReader.readString()
    print("Password: ")
    val password = CommandLineReader.readString()

    authenticate(Credentials(nick, password))
  }

  private def authenticate(credentials: Credentials): Option[User] = {
    try {
      val loggedUser = authenticating.authenticate(credentials)
      Some(loggedUser)
    }
    catch {
      case _: AuthenticationException => fail("Invalid credentials")
      case _: Throwable => fail("Unknown error")
    }
  }

  private def fail(message: String): Option[User] = {
    println(message)
    None
  }
}
