package net.mtgto.domain

import scalaz.Identity
import org.sisioh.dddbase.core.Entity
import java.util.UUID

trait User extends Entity[UUID] {
  override val identity: Identity[UUID]
  val name: String

  override def toString: String = Seq(identity, name).mkString("User(", ", ",")")
}

object User {
  def apply(anIdentity: Identity[UUID], aName: String): User = {
    new User {
      override val identity = anIdentity
      override val name = aName
    }
  }
}
