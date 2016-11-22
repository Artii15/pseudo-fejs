package na.przypale.fitter.repositories

import na.przypale.fitter.entities.User

trait UsersRepository {
  def searchByNickTerm(searchedTerm: String): Iterable[User]
  def delete(user: User): Unit
  def insertUnique(user: User): Unit
  def getByNick(nick: String): Option[User]
}
