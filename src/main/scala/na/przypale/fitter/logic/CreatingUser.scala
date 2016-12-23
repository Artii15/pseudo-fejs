package na.przypale.fitter.logic

import na.przypale.fitter.entities.{Credentials, Subscription, User}
import na.przypale.fitter.repositories.{SubscriptionsRepository, UsersRepository}
import org.mindrot.jbcrypt.BCrypt

class CreatingUser(usersRepository: UsersRepository, subscriptionsRepository: SubscriptionsRepository) {
  def create(credentials: Credentials): Unit = {
    val hashedPassword = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
    usersRepository.insertUnique(User(credentials.nick, hashedPassword))
    subscriptionsRepository.create(Subscription(credentials.nick, credentials.nick))
  }
}
