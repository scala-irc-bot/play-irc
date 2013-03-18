package net.mtgto.infrastructure

import anorm._
import anorm.SqlParser._
import java.util.UUID
import play.api.db.DB
import play.api.Play.current

trait ChannelDao {
  def findById(id: UUID): Option[Channel]
  def findAll: Seq[Channel]
  def save(channel: Channel): Unit
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
    DB.withConnection{ implicit c =>
      SQL("SELECT `id`,`name` FROM `channels`")().collect {
        case Row(id: String, name: String) => Channel(UUID.fromString(id), name)
      }.toList
    }
  }

  override def save(channel: Channel): Unit = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      val rowCount =
      SQL("""
          UPDATE `channels` SET
          `name` = {name} WHERE `id` = {id}
          """)
          .on('id -> channel.id,
              'name -> channel.name).executeUpdate()
      if (rowCount == 0) {
        SQL("INSERT INTO `channels` (`id`, `name`) VALUES ({id},{name})")
          .on('id -> channel.id, 'name -> channel.name).executeInsert()
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
