package fitter.testers.actors

import java.util.UUID

object RandomStringsGenerator {
  def generateCyclic(cycleLength: Int): Stream[String] = {
    val nicksToRepeat = generateRandomStrings(cycleLength)
    Stream.continually(nicksToRepeat).flatten
  }

  def generateRandomStrings(stringsCount: Int): Iterable[String] =
    Range.inclusive(1, stringsCount).map(_ => UUID.randomUUID().toString)
}
