package net.mtgto.infrastracture

import anorm.Row

trait BotDao {
  def findById(id: Int): Option[Bot]
  def findAll: Seq[Bot]
  def save(name: String, filename: String, config: Option[String], enabled: Boolean): Option[Bot]
  def save(bot: Bot): Option[Bot]
  /**
   * return the number of deleted rows
   */
  def delete(id: Int): Int
}

class DatabaseBotDao extends BotDao {
  protected[this] def convertRowToBot(row: Row): Bot = {
    row match {
      case Row(id: Int, name: String, filename: String, Some(config: java.sql.Clob), enabled: Byte) =>
        Bot(id, name, filename, Some(config.getSubString(1, config.length.toInt)), enabled != 0)
      case Row(id: Int, name: String, filename: String, None, enabled: Byte) =>
        Bot(id, name, filename, None, enabled != 0)
    }
  }

  override def findById(id: Int): Option[Bot] = {
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

  override def save(name: String, filename: String, config: Option[String], enabled: Boolean): Option[Bot] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("INSERT INTO `bots` (`name`,`filename`,`config`,`enabled`) VALUES ({name},{filename},{config},{enabled})")
        .on("name" -> name, "filename" -> filename, "config" -> config, "enabled" -> (if (enabled) "1" else "0")).executeInsert()
        .map(id => Bot(id.toInt, name, filename, config, enabled))
    }
  }

  override def save(bot: Bot): Option[Bot] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("UPDATE `bots` SET `name`={name},`filename`={filename},`config`={config},`enabled`={enabled} WHERE `id` = {id}")
        .on("name" -> bot.name, "filename" -> bot.filename, "config" -> bot.config, "enabled" -> (if (bot.enabled) "1" else "0"), "id" -> bot.id)
        .executeUpdate() match {
          case 1 => Some(bot)
          case _ => None
        }
    }
  }

  override def delete(id: Int): Int = {
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
