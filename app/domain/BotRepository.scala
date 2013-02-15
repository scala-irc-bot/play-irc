package net.mtgto.domain

import org.sisioh.dddbase.core.EntityResolver

trait BotRepository extends EntityResolver[Bot, Int] {
  def findAll: Seq[Bot]
}
