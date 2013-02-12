package net.mtgto.infrastracture

trait UserDao {
  def findById(id: Int): Option[User]
  def findByNameAndPassword(name: String, password: String): Option[User]
}

class DatabaseUserDao extends UserDao {
  override def findById(id: Int): Option[User] = {
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
      SQL("SELECT `id` FROM `users` WHERE `name` = {name} AND `password` = {password}").on("name" -> name, "password" -> password).as(scalar[Int].singleOpt).map {
        id => User(id, name)
      }
    }
  }
}
