package net.mtgto.infrastracture

case class Client(
  id: Option[Long],
  hostname: String,
  port: Int,
  password: Option[String],
  encoding: String,
  delay: Int,
  nickname: String,
  username: String,
  realname: String
)
