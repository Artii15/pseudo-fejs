package fitter.testers.actors.leaders.local

import akka.actor.{Address, Props}
import fitter.testers.actors.leaders.Leader
import fitter.testers.actors.leaders.remote.RegistrationRemoteLeader
import fitter.testers.commands.nodes.TaskStart
import fitter.testers.commands.registration.{CreateAccounts, StartRegistration}
import fitter.testers.config.SessionConfig
import fitter.testers.generators.RandomStringsGenerator
import fitter.testers.results.AggregatedResults
import fitter.testers.results.registration.{CreatedAccounts, RegistrationReport}

import scala.collection.mutable.ListBuffer

class RegistrationLocalLeader(nodes: Iterable[Address], sessionConfig: SessionConfig)
  extends Leader[StartRegistration, CreatedAccounts] {

  private val deploys = DeploysMaker.make(nodes)
  private val accountsNicks: ListBuffer[String] = ListBuffer.empty
  private var numberOfWorkersPerNode = 0

  override protected def makeWorker(workerId: Int): Props =
    Props(classOf[RegistrationRemoteLeader], sessionConfig).withDeploy(deploys.next())

  override protected val results: AggregatedResults[CreatedAccounts] = new RegistrationReport()

  override protected def readTask(task: StartRegistration): Unit = {
    accountsNicks.clear()
    numberOfWorkersPerNode = task.numberOfWorkersPerNode
    Stream.from(0).take(task.numberOfUniqueNicks).foreach(_ => {
      accountsNicks += RandomStringsGenerator.generateRandomString()
    })
  }

  override protected def makeTaskForWorker(workerId: Int): TaskStart =
    new CreateAccounts(numberOfWorkersPerNode, accountsNicks)
}
