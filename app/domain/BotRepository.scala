package net.mtgto.domain

import net.mtgto.infrastracture.{BotDao, DatabaseBotDao}

import org.sisioh.dddbase.core.EntityResolver

import scalaz.Identity

trait BotRepository extends EntityResolver[Bot, Int] {
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

    override def resolve(identifier: Identity[Int]): Bot = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[Int]): Option[Bot] = {
      botDao.findById(identifier.value).map {
        infraBot => Bot(Identity(infraBot.id), infraBot.name, infraBot.filename, infraBot.config, infraBot.enabled)
      }
    }

    override def contains(identifier: Identity[Int]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: Bot): Boolean = {
      resolveOption(entity.identity).isDefined
    }
  }
}
