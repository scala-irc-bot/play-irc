package net.mtgto.domain

import net.mtgto.infrastracture.{ChannelDao, DatabaseChannelDao}

import scalaz.Identity
import java.util.UUID

trait ChannelFactory {
	def apply(name: String): Option[Channel]
}

object ChannelFactory extends ChannelFactory {
  private val channelDao: ChannelDao = new DatabaseChannelDao

  override def apply(name: String): Option[Channel] = {
    val id = UUID.randomUUID
    channelDao.save(id, name).map {
      infraChannel => Channel(Identity(infraChannel.id), infraChannel.name)
    }
	}
}