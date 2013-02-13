package net.mtgto.domain

trait ClientFactory {
  def apply(hostname: String,
            port: Int,
            password: Option[String],
            encoding: String,
            delay: Int,
            nickname: String,
            username: String,
            realname: String): Client
}

