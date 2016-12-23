package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.Credentials
import na.przypale.fitter.logic.CreatingUser
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException

class CreatingUserUsingConsole(creatingUser: CreatingUser) {
  def create(): Unit = {
    try {
      print("Nick: ")
      val nick = CommandLineReader.readString()
      print("Password: ")
      val password = CommandLineReader.readString()

      creatingUser.create(Credentials(nick, password))
      println("User successfully created")
    }
    catch {
      case _: UserAlreadyExistsException => println("User with such nick already exists")
      case e: Throwable => e.printStackTrace()
    }
  }
}
