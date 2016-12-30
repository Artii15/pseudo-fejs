package fitter.testers.commands.nodes

trait PartialResults extends Serializable {
  def combine(partialResults: PartialResults): PartialResults
}

object EmptyResults extends PartialResults {
  override def combine(partialResults: PartialResults): PartialResults = partialResults
}
