package na.przypale.fitter.repositories.cassandra

import java.util
import java.util.stream.{Collectors, Stream}

import com.datastax.driver.core.{Row, Session}
import na.przypale.fitter.entities.Subscription
import na.przypale.fitter.repositories.SubscriptionsRepository

import scala.collection.JavaConverters
import scala.collection.immutable.HashSet

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

    val subscribedPeople = collectSubscribedPeople(session.execute(query).one())
    JavaConverters.collectionAsScalaIterable(subscribedPeople)
      .map(subscribedPerson => Subscription(subscriber, subscribedPerson))
  }

  private def collectSubscribedPeople(row: Row) = row match {
      case null => new java.util.HashSet[String]()
      case entry => entry.getSet("users", classOf[String])
  }
}
