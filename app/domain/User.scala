package net.mtgto.domain

import scalaz.Identity
import org.sisioh.dddbase.core.Entity

trait User extends Entity[Int] {
  override val identity: Identity[Int]
  val name: String
}
