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

  override def toString: String = Seq(identity, hostname, port, password, encoding, delay, nickname, username, realname).mkString("Client(", ", ",")")
}

object Client {
  def apply(argIdentity: Identity[Int],
            argHostname: String,
            argPort: Int,
            argPassword: Option[String],
            argEncoding: String,
            argDelay: Int,
            argNickname: String,
            argUsername: String,
            argRealname: String): Client = {
    new Client {
      override val identity: Identity[Int] = argIdentity
      override val hostname: String = argHostname
      override val port: Int = argPort
      override val password: Option[String] = argPassword
      override val encoding: String = argEncoding
      override val delay: Int = argDelay
      override val nickname: String = argNickname
      override val username: String = argUsername
      override val realname: String = argRealname
    }
  }
}
