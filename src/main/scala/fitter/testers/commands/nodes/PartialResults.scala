package fitter.testers.commands.nodes

trait PartialResults[T] extends Serializable {
  def combine(partialResults: PartialResults[T]): PartialResults[T]
}

class EmptyResults[T] extends PartialResults[T] {
  override def combine(partialResults: PartialResults[T]): PartialResults[T] = partialResults
}

object EmptyResults {
  def apply[T]: EmptyResults[T] = new EmptyResults()
}
