package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Subscription
import na.przypale.fitter.repositories.SubscriptionsRepository

class CassandraSubscriptionsRepository(session: Session) extends SubscriptionsRepository {

  private val createSubscriptionStatement = session.prepare(
    "INSERT INTO subscriptions(subscriber, subscribed_person) VALUES(:subscriber, :subscribedPerson)")
  def create(subscription: Subscription): Unit = {
    val Subscription(subscriber, subscribedPerson) = subscription
    val query = createSubscriptionStatement.bind()
      .setString("subscriber", subscriber)
      .setString("subscribedPerson", subscribedPerson)

    session.execute(query)
  }
}
