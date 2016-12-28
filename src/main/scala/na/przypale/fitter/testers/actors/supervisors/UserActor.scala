package na.przypale.fitter.testers.actors.supervisors

import akka.actor.{Actor, ActorRef, Props}
import na.przypale.fitter.Dependencies
import na.przypale.fitter.testers.commands._
import na.przypale.fitter.testers.config.RegistrationTesterConfig

import scala.annotation.tailrec
import scala.io.StdIn

class UserActor(config: RegistrationTesterConfig, dependencies: Dependencies) extends Actor {

  private var registrationTester: Option[ActorRef] = None

  override def preStart(): Unit = {
    registrationTester = Some(context.actorOf(Props(classOf[RegistrationTester], config, dependencies)))
  }

  override def receive: Receive = {
    case Start => interact()
  }

  @tailrec
  private def interact(): Unit = {
    println("1 - registration tests")
    println("2 - exit")

    StdIn.readLine() match {
      case "1" => registrationTester.foreach(_ ! Start); context.become(waiting)
      case "2" => context.system.terminate()
      case _ => interact()
    }
  }

  def waiting: Receive = {
    case End => context.become(receive); self ! Start
  }
}
