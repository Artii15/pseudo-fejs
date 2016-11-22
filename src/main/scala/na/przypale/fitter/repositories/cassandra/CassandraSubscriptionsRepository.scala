package na.przypale.fitter.repositories.cassandra

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Subscription
import na.przypale.fitter.repositories.SubscriptionsRepository

import scala.collection.JavaConverters

class CassandraSubscriptionsRepository(session: Session) extends SubscriptionsRepository {

  private lazy val createSubscriptionStatement = session.prepare(
    "INSERT INTO subscriptions(subscriber, subscribed_person) VALUES(:subscriber, :subscribedPerson)")
  def create(subscription: Subscription): Unit = {
    val Subscription(subscriber, subscribedPerson) = subscription

    val query = createSubscriptionStatement.bind()
      .setString("subscriber", subscriber)
      .setString("subscribedPerson", subscribedPerson)

    session.execute(query)
  }

  private lazy val findSubscriptionStatement = session.prepare(
    "SELECT subscribed_person FROM subscriptions WHERE subscriber = :subscriber")
  override def findSubscriptionsOf(subscriber: String): Iterable[Subscription] = {
    val query = findSubscriptionStatement.bind()
        .setString("subscriber", subscriber)

    JavaConverters.collectionAsScalaIterable(session.execute(query).all())
      .map(row => Subscription(subscriber, row.getString("subscribed_person")))
  }
}
