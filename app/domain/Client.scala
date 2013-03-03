package net.mtgto.domain

import scalaz.Identity
import org.sisioh.dddbase.core.Entity

trait Client extends Entity[Int] {
  override val identity: Identity[Int]
  val hostname: String
  val port: Int
  val password: Option[String]
  val encoding: String
  val messageDelay: Int
  val timerDelay: Int

  val nickname: String
  val username: String
  val realname: String

  override def toString: String = Seq(identity, hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname).mkString("Client(", ", ",")")
}

object Client {
  private case class DefaultClient(
    identity: Identity[Int],
    hostname: String,
    port: Int,
    password: Option[String],
    encoding: String,
    messageDelay: Int,
    timerDelay: Int,
    nickname: String,
    username: String,
    realname: String
  ) extends Client
  def apply(identity: Identity[Int],
            hostname: String,
            port: Int,
            password: Option[String],
            encoding: String,
            messageDelay: Int,
            timerDelay: Int,
            nickname: String,
            username: String,
            realname: String): Client = {
    DefaultClient(
      identity, hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname
    )
  }
}
