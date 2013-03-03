package net.mtgto.domain

import scalaz.Identity
import org.sisioh.dddbase.core.Entity
import java.util.UUID

trait Bot extends Entity[UUID] {
  override val identity: Identity[UUID]
  val name: String
  val filename: String
  val config: Option[String]
  val enabled: Boolean

  override def toString: String = Seq(identity, name, filename, config, enabled).mkString("Bot(", ", ",")")

  def toEnable: Bot
  def toDisable: Bot
}

object Bot {
  def apply(argIdentity: Identity[UUID], argName: String, argFilename: String, argConfig: Option[String], argEnabled: Boolean): Bot = {
    new Bot {
      override val identity = argIdentity
      override val name = argName
      override val filename = argFilename
      override val config = argConfig
      override val enabled = argEnabled

      override def toEnable: Bot = Bot(identity, name, filename, config, true)
      override def toDisable: Bot = Bot(identity, name, filename, config, false)
    }
  }
}
