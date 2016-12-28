package na.przypale.fitter.testers.actors

import java.util.UUID

object RandomStringsGenerator {
  def generateCyclic(cycleLength: Int): Stream[String] =
    Stream.continually(generateRandomStrings(cycleLength)).flatten

  def generateRandomStrings(stringsCount: Int): Iterable[String] =
    Range.inclusive(1, stringsCount).map(_ => UUID.randomUUID().toString)
}
