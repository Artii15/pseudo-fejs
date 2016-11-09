package na.przypale.fitter.repositories

import na.przypale.fitter.User

trait UsersRepository {
  def insert(user: User): Unit
  def getByNick(nick: String): User
}
