package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository

object CreatingUser {
  def create(usersRepository: UsersRepository): Unit = {
    print("Provide nick: ")
    usersRepository.insertUnique(User(CommandLineReader.readString()))
    println("User successfully created")
  }
}
