package fitter.testers.actors.leaders.local

import akka.actor.{Address, Deploy}
import akka.remote.RemoteScope

object DeploysMaker {

  def make(addresses: Iterable[String], systemName: String, port: Int): Iterator[Deploy] = {
    val akkaAddresses = addresses.map(address => Address("akka.tcp", systemName, address, port))
    make(akkaAddresses)
  }

  def make(addresses: Iterable[Address]): Iterator[Deploy] =
    Stream.continually(addresses).flatten.map(address => Deploy(scope = RemoteScope(address))).iterator
}
