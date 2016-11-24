package na.przypale.fitter.repositories

import na.przypale.fitter.entities.Event

trait EventsRepository {
  def create(event: Event): Unit
}
