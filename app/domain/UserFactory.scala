package net.mtgto.domain

import net.mtgto.infrastracture.{UserDao, DatabaseUserDao}

import scalaz.Identity

trait UserFactory {
  def createAdminUser(name: String, password: String): Option[User]
}

object UserFactory extends UserFactory {
  private val userDao: UserDao = new DatabaseUserDao

  override def createAdminUser(name: String, password: String): Option[User] = {
    userDao.save(name, password).map {
      infraUser => User(Identity(infraUser.id), infraUser.name)
    }
  }
}