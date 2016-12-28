package na.przypale.fitter.testers.actors.supervisors

import akka.actor.{Actor, Props}
import na.przypale.fitter.testers.commands._

import scala.annotation.tailrec
import scala.io.StdIn

class UserActor extends Actor {

  private val registrationTester = context.actorOf(Props[RegistrationTester])

  override def receive: Receive = {
    case Start => interact()
  }

  @tailrec
  private def interact(): Unit = {
    println("1 - registration tests")
    println("2 - exit")

    StdIn.readLine() match {
      case "1" => registrationTester ! Start; context.become(waitingForTaskToFinish)
      case "2" => context.system.terminate()
      case _ => interact()
    }
  }

  def waitingForTaskToFinish: Receive = {
    case TaskEnd(results) => finishTask(results)
  }

  private def finishTask(results: String): Unit = {
    println(results)
    context.become(receive)
    self ! Start
  }
}
