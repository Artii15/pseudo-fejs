package na.przypale.fitter.repositories

import java.util.Calendar

object Dates {
  def currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
}
