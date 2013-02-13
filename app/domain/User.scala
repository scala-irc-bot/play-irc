package net.mtgto.domain

import scalaz.Identity
import org.sisioh.dddbase.core.Entity

trait User extends Entity[Int] {
  override val identity: Identity[Int]
  val name: String

  override def toString: String = Seq(identity, name).mkString("User(", ", ",")")
}

object User {
  def apply(anIdentity: Identity[Int], aName: String): User = {
    new User {
      override val identity = anIdentity
      override val name = aName
    }
  }
}
