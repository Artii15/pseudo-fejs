package fitter.testers.actors.supervisors

import akka.actor.Props
import fitter.{CommandLineReader, Dependencies}
import fitter.entities.Credentials
import fitter.testers.actors.RandomStringsGenerator
import fitter.testers.actors.bots.registration.AccountsCreatorsSupervisor
import fitter.testers.commands.Start
import fitter.testers.commands.nodes.Deployment
import fitter.testers.commands.registration.{AccountsCreatingCommand, AccountsCreatingTaskEnd}
import fitter.testers.config.{SessionConfig, SystemConfig}

import scala.collection.mutable.ArrayBuffer

class RegistrationSupervisor(systemConfig: SystemConfig, sessionConfig: SessionConfig)
  extends TestsSupervisor(systemConfig, sessionConfig) {

  private val registeredAccounts: ArrayBuffer[Iterable[Credentials]] = ArrayBuffer.empty

  protected def run(): Unit = {
    print("Number of threads on each node: ")
    val numberOfThreadsOnNode = CommandLineReader.readPositiveInt()
    print("Number of unique nicks: ")
    val numberOfUniqueNicks = CommandLineReader.readPositiveInt()

    val nicks = RandomStringsGenerator.generateRandomStrings(numberOfUniqueNicks)
    val supervisorPropsGenerator = (dependencies: Dependencies) => Props(classOf[AccountsCreatorsSupervisor], dependencies)
    context.children.foreach(agent => {
      agent ! Deployment(supervisorPropsGenerator)
      agent ! AccountsCreatingCommand(numberOfThreadsOnNode, nicks)
    })
    context.become(waitingForRegistrationToFinish)
    workingNodes = numberOfNodes
    registeredAccounts.clear()
  }

  private def waitingForRegistrationToFinish: Receive = {
    case AccountsCreatingTaskEnd(createdAccounts) => collectRegistrationStatus(createdAccounts)
  }

  private def collectRegistrationStatus(createdAccounts: Iterable[Credentials]): Unit = {
    workingNodes -= 1
    registeredAccounts += createdAccounts
    if(workingNodes == 0) {
      val registeredAccountsFlatList = registeredAccounts.flatten
      println("Registered accounts credentials:")
      registeredAccountsFlatList.foreach { case Credentials(nick, password) => println(s"$nick\t$password") }
      println(s"Number of registered accounts: ${registeredAccountsFlatList.size}")
      context.become(receive)
      self ! Start
    }
  }
}
