package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.repositories.UsersRepository
import org.mindrot.jbcrypt.BCrypt

object DeletingUser {
  def delete(usersRepository: UsersRepository): Unit = {
    print("Nick: ")
    val nick = CommandLineReader.readString()

    print("Password: ")
    val password = CommandLineReader.readString()

    val userToDelete = usersRepository.getByNick(nick)
    if(BCrypt.checkpw(password, userToDelete.password))
      usersRepository.delete(userToDelete)
    else
      println("Invalid credentials")
  }
}
