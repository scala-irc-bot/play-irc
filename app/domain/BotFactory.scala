package net.mtgto.domain

import net.mtgto.infrastracture.{BotDao, DatabaseBotDao}

import scalaz.Identity

trait BotFactory {
	def apply(name: String, filename: String, config: Option[String], enabled: Boolean): Option[Bot]
}

object BotFactory extends BotFactory {
  private val botDao: BotDao = new DatabaseBotDao

  override def apply(name: String, filename: String, config: Option[String], enabled: Boolean): Option[Bot] = {
    botDao.save(name, filename, config, enabled).map {
      infraBot => Bot(Identity(infraBot.id), infraBot.name, infraBot.filename, infraBot.config, infraBot.enabled)
    }
	}
}