package na.przypale.fitter

import com.datastax.driver.core.Session

class UsersRepository(val session: Session) {

  def insert(user: User) = {
    val statement = session.prepare("INSERT INTO users(nick) VALUES(:nick)").bind().setString("nick", user.nick)
    session.execute(statement)
  }

  def getByNick(nick: String): User = {
    val query = session.prepare("SELECT nick FROM users WHERE nick = :nick").bind().setString("nick", nick)
    new User(session.execute(query).one().getString("nick"))
  }
}
