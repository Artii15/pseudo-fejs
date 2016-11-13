package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository
import org.mindrot.jbcrypt.BCrypt

object LoggingIn {
  def logIn(usersRepository: UsersRepository): Option[User] = {
    print("Nick: ")
    val nick = CommandLineReader.readString()
    print("Password: ")
    val password = CommandLineReader.readString()

    authenticate(usersRepository.getByNick(nick), password)
  }

  def authenticate(user: Option[User], password: String) = user match {
    case Some(storedUser) if BCrypt.checkpw(password, storedUser.password) => user
    case _ =>
      println("Invalid credentials")
      None
  }
}
