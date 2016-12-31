package fitter.interactions

import fitter.CommandLineReader
import fitter.entities.Credentials
import fitter.logic.CreatingUser
import fitter.logic.exceptions.InvalidCredentialsException
import fitter.repositories.exceptions.{UserAlreadyExistsException, UserNotExistsException}

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
      case _: UserNotExistsException => println("Account could not be created")
      case _: InvalidCredentialsException => println("Invalid credentials provided. Did you enter empty values?")
      case e: Throwable => e.printStackTrace()
    }
  }
}
