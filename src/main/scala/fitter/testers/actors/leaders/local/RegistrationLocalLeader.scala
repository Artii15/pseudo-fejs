package fitter.testers.actors.leaders.local

import akka.actor.{Address, Props}
import fitter.testers.actors.leaders.Leader
import fitter.testers.actors.leaders.remote.RegistrationRemoteLeader

class RegistrationLocalLeader(nodes: Iterable[Address]) extends Leader {

  private val deploys = DeploysMaker.make(nodes)

  override protected def makeWorker(workerId: Int): Props =
    Props(classOf[RegistrationRemoteLeader]).withDeploy(deploys.next())
}
