package na.przypale.fitter.repositories

import na.przypale.fitter.entities.Subscription

trait SubscriptionsRepository {
  def create(subscription: Subscription)
}
