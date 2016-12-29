package fitter.testers.commands.events

import fitter.entities.Event

case class JoinEvent(participantNick: String, event: Event)
