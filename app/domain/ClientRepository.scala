package net.mtgto.domain

import net.mtgto.infrastructure.{ClientDao, DatabaseClientDao}

import org.sisioh.dddbase.core.{EntityNotFoundException, Repository}
import scalaz.Identity
import java.util.UUID

trait ClientRepository extends Repository[Client, UUID] {
  def findHead: Option[Client]
}

object ClientRepository {
  def apply(): ClientRepository = new ClientRepository {
    import net.mtgto.infrastructure.{Client => InfraClient}

    private val clientDao: ClientDao = new DatabaseClientDao

    protected[this] def convertInfraToDomain(infraClient: InfraClient): Client = {
      Client(Identity(infraClient.id),
             infraClient.hostname,
             infraClient.port,
             infraClient.password,
             infraClient.encoding,
             infraClient.messageDelay,
             infraClient.timerDelay,
             infraClient.nickname,
             infraClient.username,
             infraClient.realname)
    }

    protected[this] def convertDomainToInfra(client: Client): InfraClient = {
      InfraClient(
        client.identity.value,
        client.hostname,
        client.port,
        client.password,
        client.encoding,
        client.messageDelay,
        client.timerDelay,
        client.nickname,
        client.username,
        client.realname)
    }

    override def findHead: Option[Client] = {
      clientDao.findAll.headOption.map(convertInfraToDomain)
    }

    /**
     * 識別子に該当するエンティティを取得する。
     *
     *  @param identifier 識別子
     *  @return エンティティ
     *
     *  @throws IllegalArgumentException
     *  @throws EntityNotFoundException エンティティが見つからなかった場合
     *  @throws RepositoryException リポジトリにアクセスできない場合
     */
    override def resolve(identifier: Identity[UUID]): Client = {
      resolveOption(identifier).getOrElse(throw new EntityNotFoundException)
    }

    override def resolveOption(identifier: Identity[UUID]): Option[Client] = {
      clientDao.findById(identifier.value).map(convertInfraToDomain)
    }

    override def contains(identifier: Identity[UUID]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: Client): Boolean = {
      resolveOption(entity.identity).isDefined
    }

    /**
     * エンティティを保存する。
     *
     * @param entity 保存する対象のエンティティ
     * @throws RepositoryException リポジトリにアクセスできない場合
     */
    override def store(entity: Client): Unit = {
      clientDao.save(convertDomainToInfra(entity))
    }

    /**
     * 指定した識別子のエンティティを削除する。
     *
     * @param identity 識別子
     * @throws EntityNotFoundException 指定された識別子を持つエンティティが見つからなかった場合
     * @throws RepositoryException リポジトリにアクセスできない場合
     */
    override def delete(identity: Identity[UUID]): Unit = {
      if (clientDao.delete(identity.value) == 0) {
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
    override def delete(entity: Client): Unit = {
      delete(entity.identity)
    }
  }
}
