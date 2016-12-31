package fitter.repositories.cassandra

import java.util.UUID

case class UsersDTO(nick: String, password: String, timeId: UUID)
