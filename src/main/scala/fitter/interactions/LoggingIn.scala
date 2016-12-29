package fitter.interactions

import fitter.CommandLineReader
import fitter.entities.{Credentials, User}
import fitter.logic.Authenticating
import fitter.logic.exceptions.AuthenticationException

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
