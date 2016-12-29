package fitter.testers.generators

import java.util.{Calendar, UUID}

import fitter.entities.Event

object EventsGenerator {
  def generateNextYearEvent(author: String, maxParticipantsCount: Int): Event = {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.YEAR, 1)
    val eventStartDate = calendar.getTime

    calendar.add(Calendar.MONTH, 1)
    val eventEndDate = calendar.getTime

    val eventName = RandomStringsGenerator.generateRandomString()
    val eventDescription = RandomStringsGenerator.generateRandomString()

    Event(UUID.randomUUID(), eventStartDate, eventEndDate, maxParticipantsCount, eventName, eventDescription, author)
  }
}
