package net.mtgto.infrastracture

import java.util.UUID

trait ChannelDao {
  def findById(id: UUID): Option[Channel]
  def findAll: Seq[Channel]
  def save(id: UUID, name: String): Option[Channel]
  /**
   * return the number of deleted rows
   */
  def delete(id: UUID): Int
}

class DatabaseChannelDao extends ChannelDao {
  override def findById(id: UUID): Option[Channel] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("SELECT `name` FROM `channels` WHERE `id` = {id}").on("id" -> id).as(scalar[String].singleOpt).map {
        name => Channel(id, name)
      }
    }
  }

  override def findAll: Seq[Channel] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("SELECT `id`,`name` FROM `channels`")().collect {
        case Row(id: String, name: String) => Channel(UUID.fromString(id), name)
      }.toList
    }
  }

  override def save(id: UUID, name: String): Option[Channel] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("INSERT INTO `channels` (`id`, `name`) VALUES ({id},{name})")
        .on('id -> id, 'name -> name).executeInsert().map { _ =>
        Channel(id, name)
      }
    }
  }

  override def delete(id: UUID): Int = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("DELETE FROM `channels` WHERE `id` = {id}")
        .on("id" -> id)
        .executeUpdate()
    }
  }
}
