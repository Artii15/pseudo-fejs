package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.UsersRepository

class CassandraUsersRepository(val session: Session) extends UsersRepository {

  def insert(user: User) = {
    val statement = session.prepare("INSERT INTO users(nick) VALUES(:nick)").bind().setString("nick", user.nick)
    session.execute(statement)
  }

  def getByNick(nick: String): User = {
    val query = session.prepare("SELECT nick FROM users WHERE nick = :nick").bind().setString("nick", nick)
    new User(session.execute(query).one().getString("nick"))
  }
}
