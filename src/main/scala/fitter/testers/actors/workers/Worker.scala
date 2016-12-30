package fitter.testers.actors.workers

import akka.actor.Actor
import fitter.testers.commands.nodes.{PartialResults, TaskStart}

abstract class Worker[TaskStartMsg <: TaskStart, PartialResultsMsg <: PartialResults] extends Actor {

  override def receive: Receive = {
    case taskStart: TaskStartMsg => receiveTaskStart(taskStart)
  }

  private def receiveTaskStart(taskStart: TaskStartMsg): Unit = {
    val results = executeTask(taskStart)
    context.parent ! results
  }

  protected def executeTask(taskStart: TaskStartMsg): PartialResultsMsg
}
