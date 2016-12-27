package na.przypale.fitter.repositories

import na.przypale.fitter.entities.{User, UsersSearchRow}
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException

trait UsersRepository {

  def searchByNickTerm(searchedTerm: String, lastRow: Option[UsersSearchRow] = None): Iterable[UsersSearchRow]

  def delete(user: User): Unit

  @throws(classOf[UserAlreadyExistsException])
  def insertUnique(user: User): Unit

  def getByNick(nick: String): Option[User]
}
