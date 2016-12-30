package fitter.testers.actors.leaders

import akka.actor.{Actor, PoisonPill, Props}
import fitter.testers.commands.nodes.{EmptyResults, PartialResults, TaskEnd, TaskStart}

abstract class Leader[TaskStartMsg <: TaskStart, TaskEndMsg <: TaskEnd, PartialResultsMsg <: PartialResults]
  extends Actor {

  private var numberOfRunningWorkers = 0
  private var results: PartialResults = EmptyResults

  def receive: Receive = {
    case taskStart: TaskStartMsg => beginTask(taskStart)
  }

  private def beginTask(taskStart: TaskStartMsg): Unit = {
    Stream.from(0).take(taskStart.workersGroupSize).foreach(workerId => {
      val props = makeWorker(workerId)
      context.actorOf(props)
    })
    numberOfRunningWorkers = taskStart.workersGroupSize
    results = EmptyResults
    context.become(collectingWorkersResults)
  }

  protected def makeWorker(workerId: Int): Props

  protected def collectingWorkersResults: Receive = {
    case partialResults: PartialResultsMsg => receivePartialResults(partialResults)
  }

  private def receivePartialResults(partialResults: PartialResultsMsg): Unit = {
    results = results.combine(partialResults)
    numberOfRunningWorkers -= 1
    if(numberOfRunningWorkers == 0) {
      context.parent ! results
      context.children.foreach(_ ! PoisonPill)
    }
  }
}
