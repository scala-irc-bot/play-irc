package net.mtgto.domain

import scalaz.Identity
import java.util.UUID

trait UserFactory {
  def createUser(name: String, password: String): User
}

object UserFactory extends UserFactory {
  override def createUser(name: String, password: String): User = {
    User(Identity(UUID.randomUUID), name, Some(password))
  }
}