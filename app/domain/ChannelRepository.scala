package net.mtgto.domain

import org.sisioh.dddbase.core.EntityResolver

trait ChannelRepository extends EntityResolver[Channel, Int] {
  def findAll: Seq[Channel]
}
