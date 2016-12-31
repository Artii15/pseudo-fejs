package fitter.interactions

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

trait BrowsingUserContent {
  val dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")

  def timeIdToDate(timeId: UUID) = new Date((timeId.timestamp() - 0x01b21dd213814000L) / 10000)
}
