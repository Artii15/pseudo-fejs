package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.Event
import na.przypale.fitter.repositories.EventsRepository
import na.przypale.fitter.repositories.exceptions.{EventParticipantAlreadyAssigned, EventParticipantLimitExceedException}

import scala.annotation.tailrec

class JoiningEvent(eventsRepository: EventsRepository) {
  def join(loggedUser: String, availableEvents: Iterable[(Event, Int)]): Unit = {
    val selectedEvent = letUserSelectEvent(availableEvents)

    try {
      eventsRepository.assignUserToEvent(selectedEvent, loggedUser)
      println("You have successfully joined this event")
    }
    catch {
      case _ : EventParticipantLimitExceedException => println("Participants limit exceeded")
      case _ : EventParticipantAlreadyAssigned => println("You have already tried to join this event")
    }
  }

  @tailrec
  private def letUserSelectEvent(availableEvents: Iterable[(Event, Int)]): Event = {
    print("Event number: ")
    val eventNr = CommandLineReader.readInt()

    availableEvents.find { case (_, number) => number == eventNr } match {
      case Some((event, _)) => event
      case _ =>
        println("Invalid event number. Try again.")
        letUserSelectEvent(availableEvents)
    }
  }
}
