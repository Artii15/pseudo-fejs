package na.przypale.fitter

import java.util.{Calendar, Date}

object Dates {
  def currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
  def extractYear(date: Date): Int = {
    val calendar = Calendar.getInstance()
    calendar.setTime(date)
    calendar.get(Calendar.YEAR)
  }
}
