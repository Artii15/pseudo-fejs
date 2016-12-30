package fitter.testers.actors.leaders.remote

import akka.actor.Props
import fitter.testers.actors.leaders.SessionOwner
import fitter.testers.actors.workers.RegistrationWorker
import fitter.testers.commands.TaskStart
import fitter.testers.commands.registration.{CreateAccount, CreateAccounts}
import fitter.testers.config.SessionConfig
import fitter.testers.results.AggregatedResults
import fitter.testers.results.registration.{CreatedAccount, CreatedAccounts}

class RegistrationRemoteLeader(sessionConfig: SessionConfig)
  extends SessionOwner[CreateAccounts, CreatedAccount](sessionConfig) {

  var nicksForWorkers: Iterator[String] = Iterator.empty

  override protected def readTask(task: CreateAccounts): Unit =
    nicksForWorkers = Stream.continually(task.nicks).flatten.iterator

  override protected def makeWorker(workerId: Int): Props =
    Props(classOf[RegistrationWorker], dependencies.creatingUser)

  override protected def makeTaskForWorker(workerId: Int): TaskStart = new CreateAccount(nicksForWorkers.next())

  override protected val results: AggregatedResults[CreatedAccount] = new CreatedAccounts()
}
