package net.mtgto.domain

import org.sisioh.dddbase.core.EntityResolver

trait UserRepository extends EntityResolver[User, Int] {
  def findByNameAndPassword(name: String, password: String): Option[User]
}
