package fitter.logic

import fitter.entities.{Credentials, Subscription, User}
import fitter.logic.exceptions.InvalidCredentialsException
import fitter.repositories.exceptions.{UserAlreadyExistsException, UserNotExistsException}
import fitter.repositories.{SubscriptionsRepository, UsersRepository}
import org.mindrot.jbcrypt.BCrypt

class CreatingUser(usersRepository: UsersRepository, subscriptionsRepository: SubscriptionsRepository) {

  @throws(classOf[UserAlreadyExistsException])
  @throws(classOf[UserNotExistsException])
  @throws(classOf[InvalidCredentialsException])
  def create(credentials: Credentials): Unit = {
    if (areValid(credentials)) saveUser(credentials) else throw new InvalidCredentialsException
  }

  private def areValid(credentials: Credentials): Boolean = !credentials.nick.isEmpty && !credentials.password.isEmpty

  private def saveUser(credentials: Credentials): Unit = {
    val hashedPassword = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
    usersRepository.insertUnique(User(credentials.nick, hashedPassword))
    subscriptionsRepository.create(Subscription(credentials.nick, credentials.nick))
  }
}
