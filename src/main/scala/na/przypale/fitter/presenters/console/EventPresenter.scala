package na.przypale.fitter.presenters.console

import na.przypale.fitter.entities.Event

object EventPresenter {
  def display(event: Event): Unit = {
    val Event(_, startDate, endDate, maxParticipantsCount, name, description, author) = event
    println(s"Name: $name")
    println(s"Author: $author")
    println(s"Duration: $startDate - $endDate")
    println(s"Max number of participants: $maxParticipantsCount")
    println(s"$description\n")
  }
}
