package net.mtgto.domain

import org.sisioh.dddbase.core.EntityResolver

trait ClientRepository extends EntityResolver[Client, Int] {
  def findHead: Option[Client]
}
