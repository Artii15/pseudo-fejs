package fitter.testers.results

abstract class AggregatedResults[T]() {
  def combine(partialResult: T): Unit
  def clear(): Unit
}
