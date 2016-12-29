package fitter.testers.commands.nodes

import akka.actor.Props
import fitter.Dependencies

case class Deployment(generateProps: (Dependencies => Props)) extends Serializable
