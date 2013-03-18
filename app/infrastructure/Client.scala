package net.mtgto.infrastructure

import java.util.UUID

case class Client(
  id: UUID,
  hostname: String,
  port: Int,
  password: Option[String],
  encoding: String,
  messageDelay: Int,
  timerDelay: Int,
  nickname: String,
  username: String,
  realname: String
)
