package na.przypale.fitter.testers.actors

import akka.actor.{AddressFromURIString, Deploy}
import akka.remote.RemoteScope

object DeployGenerator {
  def makeDeploy(system: String, address: String, port: Int): Deploy = {
    val actorNodeAddress = s"akka.tcp://$system@$address:$port"
    new Deploy(RemoteScope(AddressFromURIString(actorNodeAddress)))
  }
}
