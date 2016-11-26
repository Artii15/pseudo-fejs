package na.przypale.fitter.repositories

import na.przypale.fitter.entities.{Event, EventParticipation}

trait EventsRepository {
  def create(event: Event): Unit
  def join(eventParticipation: EventParticipation): Unit
}
