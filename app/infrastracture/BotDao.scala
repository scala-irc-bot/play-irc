package net.mtgto.infrastracture

import anorm.Row

trait BotDao {
  def findById(id: Int): Option[Bot]
  def findAll: Seq[Bot]
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
      SQL("SELECT `id`,`name`,`filename`,`config`,`enabled` FROM bots` WHERE `id` = {id}").on("id" -> id)().headOption.map(convertRowToBot)
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
}






