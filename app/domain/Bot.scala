package net.mtgto.domain

import scalaz.Identity
import org.sisioh.dddbase.core.Entity

trait Bot extends Entity[Int] {
  override val identity: Identity[Int]
  val name: String
  val config: Option[String]
  val enabled: Boolean

  override def toString: String = Seq(identity, name, config, enabled).mkString("Bot(", ", ",")")
}

object Bot {
  def apply(argIdentity: Identity[Int], argName: String, argConfig: Option[String], argEnabled: Boolean): Bot = {
    new Bot {
      override val identity = argIdentity
      override val name = argName
      override val config = argConfig
      override val enabled = argEnabled
    }
  }
}
