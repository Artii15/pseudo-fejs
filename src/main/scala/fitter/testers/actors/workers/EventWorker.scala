package fitter.testers.actors.workers

import com.datastax.driver.core.exceptions.NoHostAvailableException
import fitter.entities.Credentials
import fitter.logic.CreatingUser
import fitter.repositories.cassandra.CassandraEventsRepository
import fitter.repositories.exceptions.{EventParticipantLimitExceedException, UserAlreadyExistsException, UserNotExistsException}
import fitter.testers.commands.events.JoinEvent
import fitter.testers.generators.RandomStringsGenerator
import fitter.testers.results.events.JoinedParticipant

class EventWorker(eventsRepository: CassandraEventsRepository, creatingUser: CreatingUser)
  extends Worker[JoinEvent, JoinedParticipant] {

  override protected def executeTask(task: JoinEvent): JoinedParticipant = {
    try {
      val credentials = Credentials(RandomStringsGenerator.generateRandomString(), RandomStringsGenerator.generateRandomString())
      creatingUser.create(credentials)
      eventsRepository.assignUserToEvent(task.event, credentials.nick)
      JoinedParticipant(Some(credentials))
    }
    catch {
      case _: EventParticipantLimitExceedException
           | _: UserNotExistsException
           | _: UserAlreadyExistsException
           | _: NoHostAvailableException => JoinedParticipant(None)
    }
  }
}
