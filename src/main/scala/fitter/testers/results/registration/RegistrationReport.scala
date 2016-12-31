package fitter.testers.results.registration

import fitter.entities.Credentials
import fitter.testers.results.{AggregatedResults, TaskReport}

import scala.collection.mutable.ListBuffer

class RegistrationReport extends AggregatedResults[CreatedAccounts] with TaskReport {

  val accounts: ListBuffer[Credentials] = ListBuffer.empty

  override def combine(createdAccounts: CreatedAccounts): Unit = accounts ++= createdAccounts.accounts

  override def clear(): Unit = accounts.clear()
}
