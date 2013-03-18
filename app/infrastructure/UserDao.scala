package net.mtgto.infrastructure

import anorm._
import anorm.SqlParser._
import play.api.db.DB

import java.util.UUID

trait UserDao {
  def findById(id: UUID): Option[User]
  def findByNameAndPassword(name: String, password: String): Option[User]
  def findAll: Seq[User]
  def save(id: UUID, name: String, password: String): Unit

  /**
   * return the number of deleted rows
   */
  def delete(id: UUID): Int
}

class DatabaseUserDao extends UserDao {
  import play.api.Play.current
  override def findById(id: UUID): Option[User] = {
    DB.withConnection{ implicit c =>
      SQL("SELECT `name` FROM `users` WHERE `id` = {id}").on("id" -> id).as(scalar[String].singleOpt).map {
        name => User(id, name)
      }
    }
  }

  override def findByNameAndPassword(name: String, password: String): Option[User] = {
    DB.withConnection{ implicit c =>
      SQL("SELECT `id` FROM `users` WHERE `name` = {name} AND `password` = {password}").on("name" -> name, "password" -> password).as(scalar[String].singleOpt).map {
        id => User(UUID.fromString(id), name)
      }
    }
  }

  override def findAll: Seq[User] = {
    DB.withConnection{ implicit c =>
      SQL("SELECT `id`,`name` FROM `users`")().map( row => row match {
        case Row(id: String, name: String) => User(UUID.fromString(id), name)
      }).toList
    }
  }

  override def save(id: UUID, name: String, password: String): Unit = {
    DB.withConnection{ implicit c =>
      val rowCount =
        SQL("UPDATE `users` SET `name` = {name}, `password` = {password} WHERE `id` = {id}")
          .on('id -> id, 'name -> name, 'password -> password).executeUpdate()
      if (rowCount == 0)
        SQL("INSERT INTO `users` (`id`, `name`,`password`) VALUES ({id},{name},{password})")
          .on('id -> id, 'name -> name, 'password -> password).executeInsert()
    }
  }

  override def delete(id: UUID): Int = {
    DB.withConnection{ implicit c =>
      SQL("DELETE `users` WHERE `id` = {id}")
        .on('id -> id).executeUpdate()
    }
  }
}
