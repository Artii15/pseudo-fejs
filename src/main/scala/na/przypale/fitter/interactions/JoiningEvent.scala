package na.przypale.fitter.interactions

import java.util.UUID

import na.przypale.fitter.entities.User
import na.przypale.fitter.repositories.EventsRepository

class JoiningEvent(eventsRepository: EventsRepository) {
  def join(loggedUser: User, eventId: UUID): Unit = {
  }
}
