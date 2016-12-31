package fitter.testers.results.events

import fitter.entities.Credentials

case class JoinedParticipant(credentials: Option[Credentials]) extends Serializable
