package net.mtgto.domain

import net.mtgto.infrastructure.{ChannelDao, DatabaseChannelDao}

import scalaz.Identity
import java.util.UUID

trait ChannelFactory {
	def apply(name: String): Channel
}

object ChannelFactory extends ChannelFactory {
  private val channelDao: ChannelDao = new DatabaseChannelDao

  override def apply(name: String): Channel = {
    Channel(Identity(UUID.randomUUID), name)
	}
}