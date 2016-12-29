package fitter.testers.commands.registration

import fitter.entities.Credentials

case class AccountCreatingStatus(wasAccountCreated: Boolean, credentials: Credentials) extends Serializable
