package na.przypale.fitter.repositories.cassandra

import java.util.Date

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException

import scala.collection.JavaConverters

class CassandraUsersRepository(val session: Session) extends UsersRepository {

  def insertUnique(user: User) = {
    val userToInsert = UsersDTO(user.nick, user.password, new Date())
    forceInsert(userToInsert)

    if (getOldestByNick(userToInsert.nick).creationTime != userToInsert.creationTime) {
      deleteByNickAndCreationTime(userToInsert)
      throw new UserAlreadyExistsException
    }
  }

  private val insertUserStatement = session.prepare(
    "INSERT INTO users(nick, password, creation_time) VALUES(:nick, :password, :creationTime)")
  private def forceInsert(user: UsersDTO) {
    val statement = insertUserStatement.bind().setString("nick", user.nick)
      .setString("password", user.password)
      .setTimestamp("creationTime", user.creationTime)
    session.execute(statement)
  }

  private def getOldestByNick(nick: String) = getDTOsByNick(nick).minBy(_.creationTime)

  private val getByNickStatement = session.prepare("SELECT nick, creation_time FROM users WHERE nick = :nick")
  private def getDTOsByNick(nick: String) = {
    val query = getByNickStatement.bind().setString("nick", nick)
    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).map(row => {
      UsersDTO(row.getString("nick"), row.getString("password"), row.getTimestamp("creation_time"))
    })
  }

  private val deleteByNickAndCreationTimeStatement = session.prepare(
    "DELETE FROM users WHERE nick = :nick AND creation_time = :creationTime")
  private def deleteByNickAndCreationTime(user: UsersDTO): Unit = {
    val query = deleteByNickAndCreationTimeStatement.bind()
      .setString("nick", user.nick)
      .setTimestamp("creationTime", user.creationTime)
    session.execute(query)
  }

  def getByNick(nick: String): User = {
    val userDTO = getOldestByNick(nick)
    User(userDTO.nick, userDTO.password)
  }
}
