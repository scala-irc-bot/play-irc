package net.mtgto.infrastructure

import anorm.Row
import java.util.UUID

trait BotDao {
  def findById(id: UUID): Option[Bot]
  def findAll: Seq[Bot]
  def save(bot: Bot): Unit
  /**
   * return the number of deleted rows
   */
  def delete(id: UUID): Int
}

class DatabaseBotDao extends BotDao {
  protected[this] def convertRowToBot(row: Row): Bot = {
    row match {
      case Row(id: String, name: String, filename: String, Some(config: java.sql.Clob), enabled: Byte) =>
        Bot(UUID.fromString(id), name, filename, Some(config.getSubString(1, config.length.toInt)), enabled != 0)
      case Row(id: String, name: String, filename: String, None, enabled: Byte) =>
        Bot(UUID.fromString(id), name, filename, None, enabled != 0)
    }
  }

  override def findById(id: UUID): Option[Bot] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("SELECT `id`,`name`,`filename`,`config`,`enabled` FROM `bots` WHERE `id` = {id}").on("id" -> id)().headOption.map(convertRowToBot)
    }
  }

  override def findAll: Seq[Bot] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("SELECT `id`,`name`,`filename`,`config`,`enabled` FROM `bots`")().map(convertRowToBot).toList
    }
  }

  override def save(bot: Bot): Unit = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      val rowCount =
        SQL("UPDATE `bots` SET `name` = {name}, `filename` = {filename}, `config` = {config}, `enabled` = {enabled} WHERE `id` = {id}")
          .on('id -> bot.id,
              'name -> bot.name,
              'filename -> bot.filename,
              'config -> bot.config,
              'enabled -> (if (bot.enabled) "1" else "0"))
          .executeUpdate()
      if (rowCount == 0)
        SQL("INSERT INTO `bots` (`id`, `name`,`filename`,`config`,`enabled`) VALUES ({id},{name},{filename},{config},{enabled})")
          .on('id -> bot.id,
              'name -> bot.name,
              'filename -> bot.filename,
              'config -> bot.config,
              'enabled -> (if (bot.enabled) "1" else "0"))
          .executeInsert()
    }
  }

  override def delete(id: UUID): Int = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("DELETE FROM `bots` WHERE `id` = {id}")
        .on("id" -> id)
        .executeUpdate()
    }
  }
}
