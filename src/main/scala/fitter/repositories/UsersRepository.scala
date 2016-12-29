package fitter.repositories

import fitter.entities.{User, UsersSearchRow}
import fitter.repositories.exceptions.{UserAlreadyExistsException, UserNotExistsException}

trait UsersRepository {

  def searchByNickTerm(searchedTerm: String, lastRow: Option[UsersSearchRow] = None): Iterable[UsersSearchRow]

  def delete(user: User): Unit

  @throws(classOf[UserAlreadyExistsException])
  @throws(classOf[UserNotExistsException])
  def insertUnique(user: User): Unit

  def getByNick(nick: String): Option[User]
}
