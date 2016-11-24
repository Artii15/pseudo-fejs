package na.przypale.fitter.repositories

import na.przypale.fitter.entities.{User, UsersSearchRow}

trait UsersRepository {
  def searchByNickTerm(searchedTerm: String, lastRow: Option[UsersSearchRow] = None): Iterable[UsersSearchRow]
  def delete(user: User): Unit
  def insertUnique(user: User): Unit
  def getByNick(nick: String): Option[User]
}
