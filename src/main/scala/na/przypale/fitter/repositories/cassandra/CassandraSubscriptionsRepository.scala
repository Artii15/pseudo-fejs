package na.przypale.fitter.repositories.cassandra

import java.util.stream.{Collectors, Stream}

import com.datastax.driver.core.Session
import na.przypale.fitter.entities.Subscription
import na.przypale.fitter.repositories.SubscriptionsRepository
import scala.collection.JavaConverters

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

  private val findSubscriptionStatement = session.prepare(
    "SELECT users FROM subscriptions WHERE subscriber = :subscriber")
  override def findSubscriptionsOf(subscriber: String) = {
    val query = findSubscriptionStatement.bind()
        .setString("subscriber", subscriber)

    val subscribedPeople = session.execute(query).one()
        .getSet("users", classOf[String])

    JavaConverters.collectionAsScalaIterable(subscribedPeople)
      .map(subscribedPerson => Subscription(subscriber, subscribedPerson))
  }
}
