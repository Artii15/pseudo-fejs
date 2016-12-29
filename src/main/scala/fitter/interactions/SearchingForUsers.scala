package fitter.interactions

import fitter.{CommandLineReader, Config}
import fitter.controls.UsersBrowserControls
import fitter.entities.UsersSearchRow
import fitter.menu.{Action, ActionIntId}
import fitter.repositories.UsersRepository

import scala.annotation.tailrec

class SearchingForUsers(usersRepository: UsersRepository) {
  def search(): Unit = {
    println("Searched term: ")
    val searchedTerm = CommandLineReader.readString()
    if (searchedTerm.isEmpty) println("Searched term must not be empty") else findAndDisplay(searchedTerm)
  }

  private val browserControls = new UsersBrowserControls()

  @tailrec
  private def findAndDisplay(term: String, lastDisplayedUser: Option[UsersSearchRow] = None): Unit = {
    val users = usersRepository.searchByNickTerm(term)
    display(users)
    if(users.isEmpty || users.size < Config.DEFAULT_PAGE_SIZE) println("No more users to display")
    else {
      val Action(ActionIntId(actionId), _) = browserControls.interact()
      if(actionId == UsersBrowserControls.MORE_USERS_ACTION_ID) findAndDisplay(term, users.lastOption)
    }
  }

  private def display(users: Iterable[UsersSearchRow]) {
    users.foreach(user => println(user.nick))
  }
}
