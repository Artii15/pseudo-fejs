package na.przypale.fitter.entities

import java.util.UUID

case class Comment(postAuthor: String, postTimeId: UUID, commentTimeId: UUID, commentAuthor: String, content: String, id: UUID, parentId: UUID)
