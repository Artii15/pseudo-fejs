package na.przypale.fitter.entities

import java.util.UUID

case class  Post(author: String, timeId: UUID, content: String) extends UserContent
