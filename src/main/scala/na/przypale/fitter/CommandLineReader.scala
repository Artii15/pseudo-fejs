package na.przypale.fitter

import java.text.SimpleDateFormat
import java.util.Date

import scala.annotation.tailrec
import scala.io.StdIn

object CommandLineReader {

  @tailrec
  final def readInt(): Int = {
    try {
      StdIn.readLine().toInt
    }
    catch {
      case _: Throwable => readInt()
    }
  }

  def readString(): String = StdIn.readLine()

  @tailrec
  def readDate(format: String): Date = {
    try {
      new SimpleDateFormat(format).parse(StdIn.readLine())
    }
    catch {
      case _: Throwable => readDate(format)
    }
  }
}
