package na.przypale.fitter.interactions

import na.przypale.fitter.CommandLineReader
import na.przypale.fitter.entities.{Subscription, User}
import na.przypale.fitter.repositories.{SubscriptionsRepository, UsersRepository}

class SubscribingUser(usersRepository: UsersRepository, subscriptionsRepository: SubscriptionsRepository) {
  def createSubscription(subscriber: User): Unit = {
    print("Nickname of user you want to subscribe: ")
    val personToSubscribeNick = CommandLineReader.readString()

    usersRepository.getByNick(personToSubscribeNick) match {
      case Some(personToSubscribe) =>
        subscriptionsRepository.create(Subscription(subscriber.nick, personToSubscribe.nick))
        println("Subscription created")
      case _ => println("User with provided nick does not exist")
    }
  }
}
