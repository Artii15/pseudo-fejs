package fitter.testers.actors.supervisors

import akka.actor.Props
import fitter.Dependencies
import fitter.entities.Credentials
import fitter.testers.actors.bots.registration.AccountsCreatorsLeader
import fitter.testers.commands.nodes.{Deployment, TaskEnd, TaskStart}
import fitter.testers.commands.registration.{AccountsCreatingCommand, AccountsCreatingTaskEnd}
import fitter.testers.config.{RegistrationTestConfig, TestsSupervisorConfig}
import fitter.testers.generators.RandomStringsGenerator

import scala.collection.mutable.ArrayBuffer

class RegistrationSupervisor(testsSupervisorConfig: TestsSupervisorConfig, registrationConfig: RegistrationTestConfig)
  extends TestsSupervisor(testsSupervisorConfig) {

  private val registeredAccounts: ArrayBuffer[Iterable[Credentials]] = ArrayBuffer.empty
  private val nicks = RandomStringsGenerator.generateRandomStrings(registrationConfig.numberOfUniqueNicks)
  private val deployment = Deployment((dependencies: Dependencies) => {
      Props(classOf[AccountsCreatorsLeader], dependencies)
  })

  override protected def generateDeployment(): Deployment = deployment

  override protected def generateTaskStart(): TaskStart =
    AccountsCreatingCommand(registrationConfig.numberOfThreadsOnEachNode, nicks)

  override protected def onTaskEndOnSingleNode(taskEnd: TaskEnd): Unit = taskEnd match {
    case AccountsCreatingTaskEnd(createdAccounts) => registeredAccounts += createdAccounts
  }

  override protected def onTasksOnAllNodesFinish(): String = {
    val registeredAccountsFlatList = registeredAccounts.flatten
    val accountsCredentials = registeredAccountsFlatList.map { case Credentials(nick, password) => s"$nick\t$password" }
    val credentialsReport = s"Registered accounts credentials:\n${accountsCredentials.mkString("\n")}"
    val numberOfCreatedAccountsReport = s"Number of registered accounts: ${registeredAccountsFlatList.size}"

    s"$credentialsReport\n$numberOfCreatedAccountsReport"
  }
}
