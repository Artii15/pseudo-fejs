package fitter.testers.actors.workers

import akka.actor.Actor
import fitter.testers.commands.nodes.TaskStart

import scala.reflect.{ClassTag, classTag}

abstract class Worker[TaskStartMsg <: TaskStart: ClassTag, Results] extends Actor {

  override def receive: Receive = {
    case taskStart if classTag[TaskStartMsg].runtimeClass.isInstance(taskStart) =>
      receiveTaskStart(taskStart.asInstanceOf[TaskStartMsg])
  }

  private def receiveTaskStart(taskStart: TaskStartMsg): Unit = {
    val results = executeTask(taskStart)
    context.parent ! results
  }

  protected def executeTask(taskStart: TaskStartMsg): Results
}
