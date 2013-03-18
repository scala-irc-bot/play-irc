package net.mtgto.domain

import net.mtgto.infrastructure.{BotDao, DatabaseBotDao, Bot => InfraBot}

import org.sisioh.dddbase.core.{Repository, EntityNotFoundException}

import scalaz.Identity
import java.util.UUID

trait BotRepository extends Repository[Bot, UUID] {
  def findAll: Seq[Bot]
}

object BotRepository {
  def apply(): BotRepository = new BotRepository {
    private val botDao: BotDao = new DatabaseBotDao

    def findAll: Seq[Bot] = {
      botDao.findAll.map {
        infraBot => Bot(Identity(infraBot.id), infraBot.name, infraBot.filename, infraBot.config, infraBot.enabled)
      }
    }

    override def resolve(identifier: Identity[UUID]): Bot = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[UUID]): Option[Bot] = {
      botDao.findById(identifier.value).map {
        infraBot => Bot(Identity(infraBot.id), infraBot.name, infraBot.filename, infraBot.config, infraBot.enabled)
      }
    }

    override def contains(identifier: Identity[UUID]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: Bot): Boolean = {
      contains(entity.identity)
    }

    override def store(entity: Bot): Unit = {
      val infraBot = InfraBot(entity.identity.value, entity.name, entity.filename, entity.config, entity.enabled)
      botDao.save(infraBot)
    }

    override def delete(identity: Identity[UUID]): Unit = {
      if (botDao.delete(identity.value) == 0) {
        throw new EntityNotFoundException
      }
    }

    override def delete(entity: Bot): Unit = {
      delete(entity.identity)
    }
  }
}
