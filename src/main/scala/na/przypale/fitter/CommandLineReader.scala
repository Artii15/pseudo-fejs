package na.przypale.fitter

import java.text.SimpleDateFormat
import java.util.Date

import scala.annotation.tailrec
import scala.io.StdIn

object CommandLineReader {

  @tailrec
  def readPositiveInt(): Int = {
    val value = readInt()
    if (value > 0) value else readPositiveInt()
  }

  @tailrec
  def readInt(): Int = {
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
