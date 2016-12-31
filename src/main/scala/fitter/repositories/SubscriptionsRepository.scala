package fitter.repositories

import fitter.entities.Subscription

trait SubscriptionsRepository {
  def create(subscription: Subscription)
  def findSubscriptionsOf(subscriber: String): Iterable[Subscription]
}
