package fitter.interactions

import fitter.CommandLineReader
import fitter.entities.Credentials
import fitter.logic.CreatingUser
import fitter.repositories.exceptions.UserAlreadyExistsException

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
