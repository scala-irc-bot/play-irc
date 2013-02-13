package net.mtgto.infrastracture

trait ChannelDao {
  def findById(id: Int): Option[Channel]
  def findAll: Seq[Channel]
}

class DatabaseChannelDao extends ChannelDao {
  override def findById(id: Int): Option[Channel] = {
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
        case Row(id: Int, name: String) => Channel(id, name)
      }.toList
    }
  }
}
