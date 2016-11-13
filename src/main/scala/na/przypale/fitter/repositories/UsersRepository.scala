package na.przypale.fitter.repositories

import na.przypale.fitter.entities.User

trait UsersRepository {
  def delete(user: User): Unit
  def insertUnique(user: User): Unit
  def getByNick(nick: String): User
}
