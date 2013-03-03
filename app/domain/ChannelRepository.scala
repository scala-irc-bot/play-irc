package net.mtgto.domain

import net.mtgto.infrastracture.{ChannelDao, DatabaseChannelDao}

import org.sisioh.dddbase.core.EntityResolver
import scalaz.Identity
import java.util.UUID

trait ChannelRepository extends EntityResolver[Channel, UUID] {
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

    override def resolve(identifier: Identity[UUID]): Channel = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[UUID]): Option[Channel] = {
      channelDao.findById(identifier.value).map {
        infraChannel => Channel(Identity(infraChannel.id), infraChannel.name)
      }
    }

    override def contains(identifier: Identity[UUID]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: Channel): Boolean = {
      resolveOption(entity.identity).isDefined
    }
  }
}
