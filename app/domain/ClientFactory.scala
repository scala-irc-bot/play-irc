package net.mtgto.domain

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
            realname: String): Client
}

object ClientFactory extends ClientFactory {
  override def apply(hostname: String,
            port: Int,
            password: Option[String],
            encoding: String,
            messageDelay: Int,
            timerDelay: Int,
            nickname: String,
            username: String,
            realname: String): Client = {
    Client(Identity(UUID.randomUUID), hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname)
  }
}