package net.mtgto.domain

import net.mtgto.infrastracture.{ClientDao, DatabaseClientDao, Client => InfraClient}

import scalaz.Identity

trait ClientFactory {
  def apply(hostname: String,
            port: Int,
            password: Option[String],
            encoding: String,
            messageDelay: Int,
            timerDelay: Int,
            nickname: String,
            username: String,
            realname: String): Option[Client]
}

object ClientFactory extends ClientFactory {
  private val clientDao: ClientDao = new DatabaseClientDao

  override def apply(hostname: String,
            port: Int,
            password: Option[String],
            encoding: String,
            messageDelay: Int,
            timerDelay: Int,
            nickname: String,
            username: String,
            realname: String): Option[Client] = {
    clientDao.save(InfraClient(None, hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname)).map {
      id => Client(Identity(id.toInt), hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname)
    }
  }
}