package fitter.testers.results.registration

import fitter.entities.Credentials
import fitter.testers.results.AggregatedResults

import scala.collection.mutable.ListBuffer

class CreatedAccounts extends AggregatedResults[CreatedAccount] {
  val accounts: ListBuffer[Credentials] = ListBuffer.empty

  override def combine(account: CreatedAccount): Unit = {
    val CreatedAccount(credentials) = account
    if(credentials.isDefined) accounts += credentials.get
  }

  override def clear(): Unit = accounts.clear()
}
