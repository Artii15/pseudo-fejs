package fitter.interactions

import java.util.Date

import com.datastax.driver.core.utils.UUIDs
import fitter.{CommandLineReader, Config}
import fitter.entities.Event
import fitter.repositories.EventsRepository

import scala.annotation.tailrec

class CreatingEvent(eventsRepository: EventsRepository) {
  def create(creator: String): Unit = {
    print("Event name: ")
    val name = CommandLineReader.readString()

    print("Description: ")
    val description = CommandLineReader.readString()

    val beginDate = readBeginDate()
    val endDate = readEndDate(beginDate)
    val maxParticipantsCount = readMaxParticipantsCount()

    val event = Event(UUIDs.random(), beginDate, endDate, maxParticipantsCount, name, description, creator)
    eventsRepository.create(event)
  }

  @tailrec
  private def readBeginDate(): Date = {
    print(s"Event begin date (${Config.DATES_FORMAT}): ")
    val beginDate = CommandLineReader.readDate(Config.DATES_FORMAT)
    if (beginDate.after(new Date())) beginDate else readBeginDate()
  }

  @tailrec
  private def readEndDate(beginDate: Date): Date = {
    print(s"Event end date (${Config.DATES_FORMAT}): ")
    val endDate = CommandLineReader.readDate(Config.DATES_FORMAT)
    if(endDate.after(beginDate)) endDate else readEndDate(beginDate)
  }

  @tailrec
  private def readMaxParticipantsCount(): Int = {
    print(s"Max participants count (not more than ${Config.EVENTS_MAX_PARTICIPANTS_COUNT}): ")
    val maxParticipantsCount = CommandLineReader.readInt()

    if (maxParticipantsCount > 0 && maxParticipantsCount <= Config.EVENTS_MAX_PARTICIPANTS_COUNT)
      maxParticipantsCount
    else
      readMaxParticipantsCount()
  }
}
