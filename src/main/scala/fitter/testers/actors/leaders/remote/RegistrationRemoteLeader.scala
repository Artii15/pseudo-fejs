package fitter.testers.actors.leaders.remote

import akka.actor.Props
import fitter.testers.actors.leaders.SessionOwner
import fitter.testers.config.SessionConfig

class RegistrationRemoteLeader(sessionConfig: SessionConfig) extends SessionOwner(sessionConfig) {
  override protected def makeWorker(workerId: Int): Props = Props
}
