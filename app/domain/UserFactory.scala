package net.mtgto.domain

import net.mtgto.infrastracture.{UserDao, DatabaseUserDao}

import scalaz.Identity
import java.util.UUID

trait UserFactory {
  def createAdminUser(name: String, password: String): Option[User]
}

object UserFactory extends UserFactory {
  private val userDao: UserDao = new DatabaseUserDao

  override def createAdminUser(name: String, password: String): Option[User] = {
    val id = UUID.randomUUID
    userDao.save(id, name, password).map {
      infraUser => User(Identity(id), infraUser.name)
    }
  }
}