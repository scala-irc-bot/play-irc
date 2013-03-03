package net.mtgto.domain

import scalaz.Identity
import org.sisioh.dddbase.core.Entity
import java.util.UUID

trait Channel extends Entity[UUID] {
  override val identity: Identity[UUID]
  val name: String

  override def toString: String = Seq(identity, name).mkString("Channel(", ", ",")")
}

object Channel {
  def apply(anIdentity: Identity[UUID], aName: String): Channel = {
    new Channel {
      override val identity = anIdentity
      override val name = aName
    }
  }
}
