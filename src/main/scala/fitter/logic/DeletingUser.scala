package fitter.logic

import fitter.entities.User
import fitter.repositories.{EventsRepository, UsersRepository}

class DeletingUser(usersRepository: UsersRepository, eventsRepository: EventsRepository) {
  def delete(user: User): Unit = {
    usersRepository.delete(user)
    eventsRepository.deleteUserEvents(user.nick)
  }
}
