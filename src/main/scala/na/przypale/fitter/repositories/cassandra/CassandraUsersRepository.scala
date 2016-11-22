package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import com.datastax.driver.core.utils.UUIDs
import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository
import na.przypale.fitter.repositories.exceptions.{UserAlreadyExistsException, UserNotExistsException}

import scala.collection.JavaConverters

class CassandraUsersRepository(val session: Session) extends UsersRepository {

  override def insertUnique(user: User) = {
    val userToInsert = UsersDTO(user.nick, user.password, UUIDs.timeBased())
    forceInsert(userToInsert)

    getOldestByNick(userToInsert.nick) match {
      case None => throw new UserNotExistsException
      case Some(userDTO) if userDTO.timeId.compareTo(userToInsert.timeId) != 0  => {
        deleteByNickAndCreationTime(userToInsert)
        throw new UserAlreadyExistsException
      }
      case _ =>
    }
  }

  private val insertUserStatement = session.prepare(
    "INSERT INTO users(nick, searchable_nick, password, time_id) VALUES(:nick, :nick, :password, :timeId)")
  private def forceInsert(user: UsersDTO) {
    val statement = insertUserStatement.bind()
      .setString("nick", user.nick)
      .setString("password", user.password)
      .setUUID("timeId", user.timeId)
    session.execute(statement)
  }

  private def getOldestByNick(nick: String): Option[UsersDTO] = getDTOsByNick(nick) match {
    case Nil => None
    case users => Some(users.minBy(_.timeId))
  }

  private val getByNickStatement = session.prepare(
    "SELECT nick, password, time_id FROM users WHERE nick = :nick")
  private def getDTOsByNick(nick: String) = {
    val query = getByNickStatement.bind().setString("nick", nick)
    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).map(row => {
      UsersDTO(row.getString("nick"), row.getString("password"), row.getUUID("time_id"))
    })
  }

  private val deleteByNickAndCreationTimeStatement = session.prepare(
    "DELETE FROM users WHERE nick = :nick AND time_id = :timeId")
  private def deleteByNickAndCreationTime(user: UsersDTO): Unit = {
    val query = deleteByNickAndCreationTimeStatement.bind()
      .setString("nick", user.nick)
      .setUUID("timeId", user.timeId)
    session.execute(query)
  }

  override def getByNick(nick: String): Option[User] =
    getOldestByNick(nick).map(usersDTO => User(usersDTO.nick, usersDTO.password))

  private val deleteByNickStatement = session.prepare("DELETE FROM users WHERE nick = : nick")
  override def delete(user: User): Unit = {
    val query = deleteByNickStatement.bind()
      .setString("nick", user.nick)

    session.execute(query)
  }

  private val searchByNickStatement = session.prepare("SELECT nick, password FROM users WHERE searchable_nick LIKE :term")
  override def searchByNickTerm(searchedTerm: String): Iterable[User] = {
    val query = searchByNickStatement.bind()
      .setString("term", s"$searchedTerm%")

    JavaConverters.collectionAsScalaIterable(session.execute(query).all())
      .map(row => User(row.getString("nick"), row.getString("password")))
  }
}
