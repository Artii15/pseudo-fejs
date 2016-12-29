package fitter.interactions

import fitter.entities.User
import fitter.repositories.UsersRepository

class DeletingUser(usersRepository: UsersRepository) {
  def delete(user: User): Unit = {
    usersRepository.delete(user)
  }
}
