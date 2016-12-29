package na.przypale.fitter.testers.commands.registration

import na.przypale.fitter.entities.Credentials

case class AccountCreatingStatus(wasAccountCreated: Boolean, credentials: Credentials) extends Serializable
