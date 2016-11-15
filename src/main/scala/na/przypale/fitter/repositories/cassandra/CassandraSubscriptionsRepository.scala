package na.przypale.fitter.repositories.cassandra

import java.util.stream.{Collectors, Stream}

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Subscription
import na.przypale.fitter.repositories.SubscriptionsRepository

class CassandraSubscriptionsRepository(session: Session) extends SubscriptionsRepository {

  private val createSubscriptionStatement = session.prepare(
    "UPDATE subscriptions SET users = users + :subscribedPerson WHERE subscriber = :subscriber")
  def create(subscription: Subscription): Unit = {
    val Subscription(subscriber, subscribedPerson) = subscription

    val query = createSubscriptionStatement.bind()
      .setString("subscriber", subscriber)
      .setSet("subscribedPerson", Stream.of[String](subscribedPerson).collect(Collectors.toSet()))

    session.execute(query)
  }
}
