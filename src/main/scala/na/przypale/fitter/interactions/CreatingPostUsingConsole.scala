package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.User
import na.przypale.fitter.logic.CreatingPost

class CreatingPostUsingConsole(creatingPost: CreatingPost) {
  def create(user: User): Unit = {
    print("Content: ")
    val content = CommandLineReader.readString()

    creatingPost.create(content, user)
  }
}
