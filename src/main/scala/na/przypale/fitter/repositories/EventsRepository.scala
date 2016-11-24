package na.przypale.fitter.repositories

import java.util.UUID

import na.przypale.fitter.entities.Event

trait EventsRepository {
  def create(event: Event): Unit
  def join(eventId: UUID): Unit
}
