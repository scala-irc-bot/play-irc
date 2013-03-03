package net.mtgto.infrastracture

import java.util.UUID

trait UserDao {
  def findById(id: UUID): Option[User]
  def findByNameAndPassword(name: String, password: String): Option[User]
  def save(id: UUID, name: String, password: String): Option[User]
}

class DatabaseUserDao extends UserDao {
  override def findById(id: UUID): Option[User] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("SELECT `name` FROM `users` WHERE `id` = {id}").on("id" -> id).as(scalar[String].singleOpt).map {
        name => User(id, name)
      }
    }
  }

  override def findByNameAndPassword(name: String, password: String): Option[User] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("SELECT `id` FROM `users` WHERE `name` = {name} AND `password` = {password}").on("name" -> name, "password" -> password).as(scalar[String].singleOpt).map {
        id => User(UUID.fromString(id), name)
      }
    }
  }

  override def save(id: UUID, name: String, password: String): Option[User] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("INSERT INTO `users` (`id`, `name`,`password`) VALUES ({id},{name},{password})")
        .on("id" -> id, "name" -> name, "password" -> password).executeInsert()
        .map(_ => User(id, name))
    }
  }
}
