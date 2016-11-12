package na.przypale.fitter

import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository

object CreatingAccount {
  def create(usersRepository: UsersRepository) = (nick: String) => {
    usersRepository.insert(new User(nick))
  }
}
