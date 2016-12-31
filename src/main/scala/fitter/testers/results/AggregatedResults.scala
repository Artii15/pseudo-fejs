package fitter.testers.results

abstract class AggregatedResults[T] extends Serializable {
  def combine(partialResult: T): Unit
  def clear(): Unit
}
