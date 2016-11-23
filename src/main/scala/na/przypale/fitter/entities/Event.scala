package na.przypale.fitter.entities

import java.util.{Date, UUID}

case class Event(id: UUID, startDate: Date, endDate: Date,
                 name: String, description: String, author: String, participants: Set[String] = Set())

