package net.mtgto.infrastracture

trait BotDao {
  def findById(id: Int): Option[Bot]
  def findAll: Seq[Bot]
}

class DatabaseBotDao extends BotDao {
  override def findById(id: Int): Option[Bot] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("SELECT `name`,`config`,`enabled` FROM bots` WHERE `id` = {id}").on("id" -> id)().headOption.map {
        case Row(name: String, config: String, enabled: Boolean) => Bot(id, name, config, enabled)
      }
    }
  }

  override def findAll: Seq[Bot] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("SELECT `id`,`name`,`config`,`enabled` FROM `bots`")().map {
        case Row(id: Int, name: String, Some(config: java.sql.Clob), enabled: Byte) => Bot(id, name, config.getSubString(1, config.length.toInt), enabled != 0)
      }.toList
    }
  }
}






