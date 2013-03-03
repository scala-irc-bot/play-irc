package net.mtgto.domain

import net.mtgto.infrastracture.{ClientDao, DatabaseClientDao}

import org.sisioh.dddbase.core.EntityResolver
import scalaz.Identity
import java.util.UUID

trait ClientRepository extends EntityResolver[Client, UUID] {
  def findHead: Option[Client]
}

object ClientRepository {
  def apply(): ClientRepository = new ClientRepository {
    import net.mtgto.infrastracture.{Client => InfraClient}

    private val clientDao: ClientDao = new DatabaseClientDao

    private def convertInfraToDomain(infraClient: InfraClient): Client = {
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

    override def resolve(identifier: Identity[UUID]): Client = {
      resolveOption(identifier).get
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

    override def findHead: Option[Client] = {
      clientDao.findAll.headOption.map(convertInfraToDomain)
    }
  }
}
