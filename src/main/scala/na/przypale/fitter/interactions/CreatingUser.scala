package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException
import org.mindrot.jbcrypt.BCrypt

class CreatingUser(usersRepository: UsersRepository) {
  def create(): Unit = {
    try {
      print("Nick: ")
      val nick = CommandLineReader.readString()

      print("Password: ")
      val password = BCrypt.hashpw(CommandLineReader.readString(), BCrypt.gensalt())

      usersRepository.insertUnique(User(nick, password))
      println("User successfully created")
    }
    catch {
      case _: UserAlreadyExistsException => println("User with such nick already exists")
      case e: Throwable => e.printStackTrace()
    }
  }
}
