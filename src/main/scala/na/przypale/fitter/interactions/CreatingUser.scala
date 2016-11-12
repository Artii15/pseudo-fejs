package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException

object CreatingUser {
  def create(usersRepository: UsersRepository): Unit = {
    try {
      print("Provide nick: ")
      usersRepository.insertUnique(User(CommandLineReader.readString()))
      println("User successfully created")
    }
    catch {
      case _: UserAlreadyExistsException => println("User with such nick already exists")
      case _: Throwable => println("Unexpected error")
    }
  }
}
