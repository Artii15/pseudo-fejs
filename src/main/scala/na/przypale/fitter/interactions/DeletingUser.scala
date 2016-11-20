package na.przypale.fitter.interactions

import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository

class DeletingUser(usersRepository: UsersRepository) {
  def delete(user: User): Unit = {
    usersRepository.delete(user)
  }
}
