package na.przypale.fitter.repositories

import na.przypale.fitter.entities.Event

trait EventsRepository {
  def create(event: Event): Unit
  def findIncoming(): Stream[Event]
  def assignUserToEvent(event: Event, user: String): Unit
}
