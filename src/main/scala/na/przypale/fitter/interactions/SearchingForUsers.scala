package na.przypale.fitter.interactions

import na.przypale.fitter.{CommandLineReader, Config}
import na.przypale.fitter.controls.UsersBrowserControls
import na.przypale.fitter.entities.UsersSearchRow
import na.przypale.fitter.menu.{Action, ActionIntId}
import na.przypale.fitter.repositories.UsersRepository

import scala.annotation.tailrec

class SearchingForUsers(usersRepository: UsersRepository) {
  def search(): Unit = {
    println("Searched term: ")
    findAndDisplay(CommandLineReader.readString())
  }

  private val browserControls = new UsersBrowserControls()

  @tailrec
  private def findAndDisplay(term: String, lastDisplayedUser: Option[UsersSearchRow] = None): Unit = {
    val users = usersRepository.searchByNickTerm(term)
    if(users.isEmpty || users.size < Config.DEFAULT_PAGE_SIZE) println("No more posts to display")
    else {
      display(users)
      val Action(ActionIntId(actionId), _) = browserControls.interact()
      if(actionId == UsersBrowserControls.MORE_USERS_ACTION_ID) findAndDisplay(term, users.lastOption)
    }
  }

  private def display(users: Iterable[UsersSearchRow]) {
    users.foreach(user => println(user.nick))
  }
}
