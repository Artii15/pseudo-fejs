package na.przypale.fitter.repositories

import na.przypale.fitter.entities.{Event, EventParticipationRequest}

trait EventsRepository {
  def create(event: Event): Unit
  def join(eventParticipation: EventParticipationRequest): Unit
}
