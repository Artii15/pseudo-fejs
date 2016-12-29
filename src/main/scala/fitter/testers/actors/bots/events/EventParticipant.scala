package fitter.testers.actors.bots.events

import akka.actor.Actor
import fitter.Dependencies
import fitter.entities.Event
import fitter.repositories.exceptions.EventParticipantLimitExceedException
import fitter.testers.commands.events.{JoinEvent, JoiningStatus}

class EventParticipant(dependencies: Dependencies) extends Actor {
  override def receive: Receive = {
    case JoinEvent(nick, event) => joinEvent(nick, event)
  }

  private def joinEvent(nick: String, event: Event): Unit = {
    try {
      dependencies.eventsRepository.assignUserToEvent(event, nick)
      context.parent ! JoiningStatus(nick, joined = true)
    }
    catch {
      case _: EventParticipantLimitExceedException => context.parent ! JoiningStatus(nick, joined = false)
    }
  }
}
