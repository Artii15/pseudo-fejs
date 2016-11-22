package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.repositories.UsersRepository

class SearchingForUsers(usersRepository: UsersRepository) {
  def search(): Unit = {
    println("Searched term: ")
    val searchedTerm = CommandLineReader.readString()

    usersRepository.searchByNickTerm(searchedTerm).foreach(user => println(user.nick))
  }
}
