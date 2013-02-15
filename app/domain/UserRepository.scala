package net.mtgto.domain

import net.mtgto.infrastracture.{UserDao, DatabaseUserDao}

import org.sisioh.dddbase.core.EntityResolver

import scalaz.Identity

trait UserRepository extends EntityResolver[User, Int] {
  def findByNameAndPassword(name: String, password: String): Option[User]
}

object UserRepository {
  def apply(): UserRepository = new UserRepository {
    private val userDao: UserDao = new DatabaseUserDao

    def findByNameAndPassword(name: String, password: String): Option[User] = {
      userDao.findByNameAndPassword(name, password).map {
        infraUser => User(Identity(infraUser.id), infraUser.name)
      }
    }

    override def resolve(identifier: Identity[Int]): User = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[Int]): Option[User] = {
      userDao.findById(identifier.value).map {
        infraUser => User(Identity(infraUser.id), infraUser.name)
      }
    }

    override def contains(identifier: Identity[Int]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: User): Boolean = {
      resolveOption(entity.identity).isDefined
    }
  }
}
