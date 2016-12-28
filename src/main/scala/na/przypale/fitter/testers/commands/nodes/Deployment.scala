package na.przypale.fitter.testers.commands.nodes

import akka.actor.Props
import na.przypale.fitter.Dependencies

case class Deployment(id: String, generateProps: (Dependencies => Props))
