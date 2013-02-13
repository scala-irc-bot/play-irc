package net.mtgto.domain

import scalaz.Identity
import org.sisioh.dddbase.core.Entity

trait Channel extends Entity[Int] {
  override val identity: Identity[Int]
  val name: String

  override def toString: String = Seq(identity, name).mkString("Channel(", ", ",")")
}

object Channel {
  def apply(anIdentity: Identity[Int], aName: String): Channel = {
    new Channel {
      override val identity = anIdentity
      override val name = aName
    }
  }
}
