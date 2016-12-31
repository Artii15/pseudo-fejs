package fitter.interactions

import fitter.CommandLineReader
import fitter.entities.User
import fitter.logic.CreatingPost

class CreatingPostUsingConsole(creatingPost: CreatingPost) {
  def create(user: User): Unit = {
    print("Content: ")
    val content = CommandLineReader.readString()

    creatingPost.create(content, user)
  }
}
