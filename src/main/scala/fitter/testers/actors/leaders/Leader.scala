package fitter.testers.actors.leaders

import akka.actor.{Actor, PoisonPill, Props}
import fitter.testers.commands.nodes.{EmptyResults, GroupTaskStart, PartialResults}

import scala.reflect.{ClassTag, classTag}

abstract class Leader[TaskStartMsg <: GroupTaskStart: ClassTag, PartialResultsMsg <: PartialResults: ClassTag]
  extends Actor {

  private var numberOfRunningWorkers = 0
  private var results: PartialResults = EmptyResults

  def receive: Receive = {
    case taskStart if classTag[TaskStartMsg].runtimeClass.isInstance(taskStart) =>
      beginTask(taskStart.asInstanceOf[TaskStartMsg])
  }

  private def beginTask(taskStart: TaskStartMsg): Unit = {
    Stream.from(0).take(taskStart.groupSize).foreach(workerId => {
      val props = makeWorker(workerId)
      context.actorOf(props)
    })
    numberOfRunningWorkers = taskStart.groupSize
    results = EmptyResults
    context.become(collectingWorkersResults)
  }

  protected def makeWorker(workerId: Int): Props

  protected def collectingWorkersResults: Receive = {
    case partialResults if classTag[PartialResultsMsg].runtimeClass.isInstance(partialResults) =>
      receivePartialResults(partialResults.asInstanceOf[PartialResultsMsg])
  }

  private def receivePartialResults(partialResults: PartialResultsMsg): Unit = {
    results = results.combine(partialResults)
    numberOfRunningWorkers -= 1
    if(numberOfRunningWorkers == 0) {
      context.parent ! results
      context.children.foreach(_ ! PoisonPill)
      context.become(receive)
    }
  }
}
