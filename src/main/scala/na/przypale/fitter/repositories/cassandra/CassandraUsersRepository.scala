package na.przypale.fitter.repositories.cassandra

import java.util.Date

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository
import na.przypale.fitter.repositories.exceptions.UserAlreadyExistsException

import scala.collection.JavaConverters

class CassandraUsersRepository(val session: Session) extends UsersRepository {

  private val insertUserStatement = session.prepare(
    "INSERT INTO users(nick, creation_time) VALUES(:nick, :creationTime)")
  def insertUnique(user: User) = {
    val creationTime = new Date()
    val statement = insertUserStatement.bind()
      .setString("nick", user.nick)
      .setTimestamp("creationTime", creationTime)
    session.execute(statement)

    if (getDTOsByNick(user.nick).minBy(_.creationTime).creationTime != creationTime) {
      deleteByNickAndCreationTime(user.nick, creationTime)
      throw new UserAlreadyExistsException
    }
  }

  private val deleteByNickAndCreationTimeStatement = session.prepare(
    "DELETE FROM users WHERE nick = :nick AND creation_time = :creationTime")
  private def deleteByNickAndCreationTime(nick: String, creationTime: Date): Unit = {
    val query = deleteByNickAndCreationTimeStatement.bind()
      .setString("nick", nick)
      .setTimestamp("creationTime", creationTime)
    session.execute(query)
  }

  private val getByNickStatement = session.prepare("SELECT nick, creation_time FROM users WHERE nick = :nick")
  def getByNick(nick: String): User = {
    val minUserDTO = getDTOsByNick(nick).minBy(_.creationTime)
    User(minUserDTO.nick)
  }

  private def getDTOsByNick(nick: String) = {
    val query = getByNickStatement.bind().setString("nick", nick)
    JavaConverters.collectionAsScalaIterable(session.execute(query).all()).map(row => {
      UsersDTO(row.getString("nick"), row.getTimestamp("creation_time"))
    })
  }

}
