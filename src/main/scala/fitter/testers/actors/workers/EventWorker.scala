package fitter.testers.actors.workers

import fitter.entities.{Credentials, Event}
import fitter.logic.CreatingUser
import fitter.repositories.cassandra.CassandraEventsRepository
import fitter.repositories.exceptions.{EventParticipantLimitExceedException, UserAlreadyExistsException, UserNotExistsException}
import fitter.testers.commands.events.JoinEvent
import fitter.testers.generators.RandomStringsGenerator
import fitter.testers.results.events.JoinedParticipant

class EventWorker(eventsRepository: CassandraEventsRepository, creatingUser: CreatingUser)
  extends Worker[JoinEvent, JoinedParticipant] {

  override protected def executeTask(task: JoinEvent): JoinedParticipant = {
    val credentials = makeAccount()
    JoinedParticipant(joinEvent(credentials, task.event))
  }

  private def makeAccount(): Credentials = {
    val credentials = Credentials(RandomStringsGenerator.generateRandomString(), RandomStringsGenerator.generateRandomString())
    creatingUser.create(credentials)
    credentials
  }

  private def joinEvent(credentials: Credentials, event: Event): Option[Credentials] = {
    try {
      eventsRepository.assignUserToEvent(event, credentials.nick)
      Some(credentials)
    }
    catch {
      case _: EventParticipantLimitExceedException | _: UserNotExistsException | _: UserAlreadyExistsException => None
    }
  }
}
