package na.przypale.fitter.logic

import na.przypale.fitter.entities.{Credentials, User}
import na.przypale.fitter.logic.exceptions.AuthenticationException
import na.przypale.fitter.repositories.UsersRepository
import org.mindrot.jbcrypt.BCrypt

class Authenticating(usersRepository: UsersRepository) {
  def authenticate(credentials: Credentials): User = usersRepository.getByNick(credentials.nick) match {
    case Some(user) if BCrypt.checkpw(credentials.password, user.password) => user
    case _ => throw new AuthenticationException
  }
}
