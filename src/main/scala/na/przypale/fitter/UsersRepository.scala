package na.przypale.fitter

import com.datastax.driver.core.Session

class UsersRepository(val session: Session) {

  def insert(user: User) = {
    session.prepare("INSERT INTO users(nick) VALUES(:nick)").bind()
      .setString("nick", user.nick)
  }
}
