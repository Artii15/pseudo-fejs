package na.przypale.fitter.interactions

import java.util.UUID

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.repositories.EventsRepository

class JoiningEvent(eventsRepository: EventsRepository) {
  def join(participant: String): Unit = {
    print("Event id: ")

    val eventId = UUID.fromString(CommandLineReader.readString())
    //eventsRepository.join(EventParticipationRequest(participant, eventId))
  }
}
