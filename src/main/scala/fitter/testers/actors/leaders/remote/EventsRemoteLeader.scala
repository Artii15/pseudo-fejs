package fitter.testers.actors.leaders.remote

import fitter.testers.actors.leaders.SessionOwner
import fitter.testers.commands.events.MakeParticipants
import fitter.testers.config.SessionConfig
import fitter.testers.results.events.JoinedParticipants

class EventsRemoteLeader(sessionConfig: SessionConfig)
  extends SessionOwner[MakeParticipants, JoinedParticipants](sessionConfig) {

}
