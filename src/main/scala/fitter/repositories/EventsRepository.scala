package fitter.repositories

import fitter.entities.Event

trait EventsRepository {
  def create(event: Event): Unit
  def findAllIncoming(): Stream[Event]
  def assignUserToEvent(event: Event, user: String): Unit
  def findUserIncomingEvents(user: String): Stream[Event]
  def leave(event: Event, user: String): Unit
  def deleteUserEvents(user: String): Unit
}
