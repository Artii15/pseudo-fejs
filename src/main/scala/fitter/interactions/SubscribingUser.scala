package fitter.interactions

import fitter.CommandLineReader
import fitter.entities.{Subscription, User}
import fitter.repositories.{SubscriptionsRepository, UsersRepository}

class SubscribingUser(usersRepository: UsersRepository, subscriptionsRepository: SubscriptionsRepository) {
  def createSubscription(subscriber: User): Unit = {
    print("Nickname of user you want to subscribe: ")
    val subscribedUserNick = CommandLineReader.readString()

    if (subscribedUserNick.isEmpty)
      println("Nick must not be empty")
    else
      makeSubscription(subscriber, subscribedUserNick)

  }

  private def makeSubscription(subscriber: User, subscribedUserNick: String): Unit = {
    usersRepository.getByNick(subscribedUserNick) match {
      case Some(personToSubscribe) =>
        subscriptionsRepository.create(Subscription(subscriber.nick, personToSubscribe.nick))
        println("Subscription created")
      case _ => println("User with provided nick does not exist")
    }
  }
}
