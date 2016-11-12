package na.przypale.fitter

import scala.annotation.tailrec
import scala.io.StdIn

object CommandLineReader {

  @tailrec
  final def readInt(): Int = {
    try {
      val selectedCommand = StdIn.readLine()
      selectedCommand.toInt
    }
    catch {
      case _: Throwable => readInt()
    }
  }

  def readString() = StdIn.readLine()
}
