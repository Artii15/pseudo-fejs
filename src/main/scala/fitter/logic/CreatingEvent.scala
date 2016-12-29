package fitter.logic

import java.util.Date

import fitter.Config
import fitter.entities.Event
import fitter.logic.exceptions.InvalidEventData
import fitter.repositories.EventsRepository

class CreatingEvent(eventsRepository: EventsRepository) {
  def create(event: Event): Unit = {
    scanForValidationErrors(event) match {
      case None => eventsRepository.create(event)
      case Some(reason) => throw new InvalidEventData(reason)
    }
  }

  private def scanForValidationErrors(event: Event): Option[String] = {
    if (!event.startDate.after(new Date())) Some("Event must begin in future")
    else if (!event.endDate.after(event.startDate)) Some("Event must end after beginning")
    else if (event.name.isEmpty) Some("Event name must not be empty")
    else if (event.description.isEmpty) Some("Event description must not be empty")
    else if (event.maxParticipantsCount <= 0 || event.maxParticipantsCount > Config.EVENTS_MAX_PARTICIPANTS_COUNT)
      Some("Invalid participants count")
    else None
  }
}
