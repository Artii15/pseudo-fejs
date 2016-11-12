package na.przypale.fitter.logic

import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository

object CreatingAccount {
  def create(usersRepository: UsersRepository) = (nick: String) => {
    usersRepository.insert(User(nick))
  }
}
