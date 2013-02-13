package net.mtgto.domain

import scalaz.Identity
import org.sisioh.dddbase.core.Entity

trait Client extends Entity[Int] {
  override val identity: Identity[Int]
  val hostname: String
  val port: Int
  val password: Option[String]
  val encoding: String
  val delay: Int

  val nickname: String
  val username: String
  val realname: String
}

object Client {
  def apply(identity: Identity[Int],
            hostname: String,
            port: Int,
            password: Option[String],
            encoding: String,
            delay: Int,
            nickname: String,
            username: String,
            realname: String): Client = {
    new Client {
      override val identity: Identity[Int] = identity
      override val hostname: String = hostname
      override val port: Int = port
      override val password: Option[String] = password
      override val encoding: String = encoding
      override val delay: Int = delay
      override val nickname: String = nickname
      override val username: String = username
      override val realname: String = realname
    }
  }
}
