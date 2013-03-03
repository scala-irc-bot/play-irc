package net.mtgto.domain

import scalaz.Identity
import java.util.UUID

trait BotFactory {
	def apply(name: String, filename: String, config: Option[String], enabled: Boolean): Bot
}

object BotFactory extends BotFactory {
  override def apply(name: String, filename: String, config: Option[String], enabled: Boolean): Bot = {
    Bot(Identity(UUID.randomUUID), name, filename, config, enabled)
	}
}