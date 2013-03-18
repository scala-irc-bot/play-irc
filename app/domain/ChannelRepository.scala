package net.mtgto.domain

import net.mtgto.infrastructure.{ChannelDao, DatabaseChannelDao, Channel => InfraChannel}

import org.sisioh.dddbase.core.{EntityNotFoundException, Repository}
import scalaz.Identity
import java.util.UUID

trait ChannelRepository extends Repository[Channel, UUID] {
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

    /**
     * エンティティを保存する。
     *
     * @param entity 保存する対象のエンティティ
     * @throws RepositoryException リポジトリにアクセスできない場合
     */
    override def store(entity: Channel): Unit = {
      channelDao.save(InfraChannel(entity.identity.value, entity.name))
    }

    /**
     * 指定した識別子のエンティティを削除する。
     *
     * @param identity 識別子
     * @throws EntityNotFoundException 指定された識別子を持つエンティティが見つからなかった場合
     * @throws RepositoryException リポジトリにアクセスできない場合
     */
    override def delete(identity: Identity[UUID]): Unit = {
      if (channelDao.delete(identity.value) == 0) {
        throw new EntityNotFoundException
      }
    }

    /**
     * 指定したエンティティを削除する。
     *
     * @param entity エンティティ
     * @throws EntityNotFoundException 指定された識別子を持つエンティティが見つからなかった場合
     * @throws RepositoryException リポジトリにアクセスできない場合
     */
    override def delete(entity: Channel): Unit = {
      delete(entity.identity)
    }
  }
}
