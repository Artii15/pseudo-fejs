package fitter.interactions

import java.util.Date

import com.datastax.driver.core.utils.UUIDs
import fitter.{CommandLineReader, Config}
import fitter.entities.Event
import fitter.logic.CreatingEvent
import fitter.logic.exceptions.InvalidEventData

import scala.annotation.tailrec

class CreatingEventUsingConsole(creatingEvent: CreatingEvent) {

  @tailrec
  final def create(creator: String): Unit = {
    val event = gatherEventData(creator)
    try {
      creatingEvent.create(event)
    }
    catch {
      case error: InvalidEventData => println(error.reason); create(creator)
    }
  }

  private def gatherEventData(creator: String): Event = {
    print("Event name: ")
    val name = CommandLineReader.readString()

    print("Description: ")
    val description = CommandLineReader.readString()

    val beginDate = readBeginDate()
    val endDate = readEndDate(beginDate)
    val maxParticipantsCount = readMaxParticipantsCount()

    Event(UUIDs.random(), beginDate, endDate, maxParticipantsCount, name, description, creator)
  }

  private def readBeginDate(): Date = {
    print(s"Event begin date (${Config.DATES_FORMAT}): ")
    CommandLineReader.readDate(Config.DATES_FORMAT)
  }

  private def readEndDate(beginDate: Date): Date = {
    print(s"Event end date (${Config.DATES_FORMAT}): ")
    CommandLineReader.readDate(Config.DATES_FORMAT)
  }

  private def readMaxParticipantsCount(): Int = {
    print(s"Max participants count (not more than ${Config.EVENTS_MAX_PARTICIPANTS_COUNT}): ")
    CommandLineReader.readInt()
  }
}
