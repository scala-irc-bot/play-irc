package net.mtgto.domain

import net.mtgto.infrastracture.{UserDao, DatabaseUserDao}

import org.sisioh.dddbase.core.EntityResolver

import scalaz.Identity
import java.util.UUID

trait UserRepository extends EntityResolver[User, UUID] {
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

    override def resolve(identifier: Identity[UUID]): User = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[UUID]): Option[User] = {
      userDao.findById(identifier.value).map {
        infraUser => User(Identity(infraUser.id), infraUser.name)
      }
    }

    override def contains(identifier: Identity[UUID]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: User): Boolean = {
      resolveOption(entity.identity).isDefined
    }
  }
}
