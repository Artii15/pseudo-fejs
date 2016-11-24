package na.przypale.fitter.interactions

import na.przypale.fitter.entities.EnumeratedPost

class DisplayingPost {
  def display(enumeratedPost: EnumeratedPost): Unit = {
    println(s"Displayed post content:\n ${enumeratedPost.post.content}")
  }
}
