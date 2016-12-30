package fitter.testers.commands.registrationOld

import fitter.entities.Credentials

case class AccountCreatingStatus(wasAccountCreated: Boolean, credentials: Credentials) extends Serializable
