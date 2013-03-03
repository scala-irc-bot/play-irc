package net.mtgto.domain

import net.mtgto.infrastracture.{ClientDao, DatabaseClientDao, Client => InfraClient}

import scalaz.Identity
import java.util.UUID

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
    val id = UUID.randomUUID
    clientDao.save(
      InfraClient(id, hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname))
    Some(Client(Identity(id), hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname))
  }
}