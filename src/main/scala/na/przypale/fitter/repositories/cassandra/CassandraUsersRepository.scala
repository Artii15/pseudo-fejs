package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository

class CassandraUsersRepository(val session: Session) extends UsersRepository {

  private val insertUserStatement = session.prepare("INSERT INTO users(nick) VALUES(:nick)")
  private val getByNickStatement = session.prepare("SELECT nick FROM users WHERE nick = :nick")

  def insert(user: User) = {
    val statement = insertUserStatement.bind().setString("nick", user.nick)
    session.execute(statement)
  }

  def getByNick(nick: String): User = {
    val query = getByNickStatement.bind().setString("nick", nick)
    User(session.execute(query).one().getString("nick"))
  }
}
