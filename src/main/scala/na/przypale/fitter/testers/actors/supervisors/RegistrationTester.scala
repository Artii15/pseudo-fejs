package na.przypale.fitter.testers.actors.supervisors

import java.util.UUID

import akka.actor.{Actor, Props}
import na.przypale.fitter.logic.CreatingUser
import na.przypale.fitter.testers.commands.{AccountCreateCommand, Start}
import na.przypale.fitter.testers.config.RegistrationTesterConfig

class RegistrationTester(config: RegistrationTesterConfig, creatingUser: CreatingUser) extends Actor {

  override def preStart(): Unit = {
    generateNicks().take(config.numberOfProcesses).foreach(nick => {
      val accountCreator = context.actorOf(Props(classOf[RegistrationTester], creatingUser))
      accountCreator ! AccountCreateCommand(nick)
    })
  }

  private def generateNicks(): Stream[String] = Stream.continually(Stream.from(1).take(config.numberOfUniqueNicks))
    .flatten.map(_ => UUID.randomUUID().toString)

  override def receive: Receive = {
    case Start =>
  }
}
