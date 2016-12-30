package fitter.testers.actors.leaders

import akka.actor.{Actor, PoisonPill, Props}
import fitter.testers.commands.nodes.{GroupTaskStart, TaskStart}
import fitter.testers.results.AggregatedResults

import scala.reflect.{ClassTag, classTag}

abstract class Leader[LeaderTask <: GroupTaskStart: ClassTag, WorkerResults: ClassTag]
  extends Actor {

  private var numberOfRunningWorkers = 0
  protected val results: AggregatedResults[WorkerResults]

  def receive: Receive = {
    case taskStart if classTag[LeaderTask].runtimeClass.isInstance(taskStart) =>
      beginTask(taskStart.asInstanceOf[LeaderTask])
  }

  private def beginTask(taskStart: LeaderTask): Unit = {
    readTask(taskStart)
    Stream.from(0).take(taskStart.groupSize).foreach(workerId => {
      val props = makeWorker(workerId)
      context.actorOf(props) ! makeTaskForWorker(workerId)
    })
    numberOfRunningWorkers = taskStart.groupSize
    results.clear()
    context.become(collectingWorkersResults)
  }

  protected def readTask(task: LeaderTask): Unit

  protected def makeWorker(workerId: Int): Props

  protected def makeTaskForWorker(workerId: Int): TaskStart

  protected def collectingWorkersResults: Receive = {
    case partialResults if classTag[WorkerResults].runtimeClass.isInstance(partialResults) =>
      receivePartialResults(partialResults.asInstanceOf[WorkerResults])
  }

  private def receivePartialResults(partialResults: WorkerResults): Unit = {
    results.combine(partialResults)
    numberOfRunningWorkers -= 1
    if(numberOfRunningWorkers == 0) {
      context.parent ! results
      context.children.foreach(_ ! PoisonPill)
      context.become(receive)
    }
  }
}
