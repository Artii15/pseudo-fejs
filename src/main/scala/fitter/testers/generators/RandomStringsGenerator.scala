package fitter.testers.generators

import java.util.UUID

object RandomStringsGenerator {
  def generateCyclic(cycleLength: Int): Stream[String] = {
    val nicksToRepeat = generateRandomStrings(cycleLength)
    Stream.continually(nicksToRepeat).flatten
  }

  def generateRandomStrings(stringsCount: Int): Iterable[String] =
    Range.inclusive(1, stringsCount).map(_ => generateRandomString())

  def generateRandomString(): String = UUID.randomUUID().toString
}
