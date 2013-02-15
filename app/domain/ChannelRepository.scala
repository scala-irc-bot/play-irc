package net.mtgto.domain

import net.mtgto.infrastracture.{ChannelDao, DatabaseChannelDao}

import org.sisioh.dddbase.core.EntityResolver

import scalaz.Identity

trait ChannelRepository extends EntityResolver[Channel, Int] {
  def findAll: Seq[Channel]
}

object ChannelRepository {
  def apply(): ChannelRepository = new ChannelRepository {
    private val channelDao: ChannelDao = new DatabaseChannelDao

    def findAll: Seq[Channel] = {
      channelDao.findAll.map {
        infraChannel => Channel(Identity(infraChannel.id), infraChannel.name)
      }
    }

    override def resolve(identifier: Identity[Int]): Channel = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[Int]): Option[Channel] = {
      channelDao.findById(identifier.value).map {
        infraChannel => Channel(Identity(infraChannel.id), infraChannel.name)
      }
    }

    override def contains(identifier: Identity[Int]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: Channel): Boolean = {
      resolveOption(entity.identity).isDefined
    }
  }
}
