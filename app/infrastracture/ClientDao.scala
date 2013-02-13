package net.mtgto.infrastracture

import anorm.Row

trait ClientDao {
  def findById(id: Int): Option[Client]

  def findAll: Seq[Client]

  def save(client: Client): Option[Long]
}

class DatabaseClientDao extends ClientDao {
  protected[this] def convertRowToClient(row: Row) = {
    row match {
      case Row(id: Int, hostname: String, port: Int, Some(password: String), encoding: String, delay: Int, nickname: String, username: String, realname: String) =>
        Client(Some(id), hostname, port, Some(password), encoding, delay, nickname, username, realname)
      case Row(id: Int, hostname: String, port: Int, None, encoding: String, delay: Int, nickname: String, username: String, realname: String) =>
        Client(Some(id), hostname, port, None, encoding, delay, nickname, username, realname)
    }
  }
  override def findById(id: Int): Option[Client] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("SELECT `hostname`,`port`,`password`,`encoding`,`delay`,`nickname`,`username`,`realname` FROM `clients` WHERE `id` = {id}")
                      .on("id" -> id)().headOption.map(convertRowToClient)
    }
  }

  override def findAll: Seq[Client] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("SELECT `id`,`hostname`,`port`,`password`,`encoding`,`delay`,`nickname`,`username`,`realname` FROM `clients`")().collect {
        case Row(id: Int, hostname: String, port: Int, Some(password: String), encoding: String, delay: Int, nickname: String, username: String, realname: String) =>
          Client(Some(id), hostname, port, Some(password), encoding, delay, nickname, username, realname)
        case Row(id: Int, hostname: String, port: Int, None, encoding: String, delay: Int, nickname: String, username: String, realname: String) =>
          Client(Some(id), hostname, port, None, encoding, delay, nickname, username, realname)
      }
    }
  }

  override def save(client: Client): Option[Long] = {
    import anorm._
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    DB.withConnection{ implicit c =>
      SQL("""
          INSERT INTO `client` (`hostname`,`port`,`password`,`encoding`,`delay`,`nickname`,`username`,`realname`)
          VALUES ({hostname}, {port}, {password}, {encoding}, {delay}, {nickname}, {username}, {realname})
          """)
          .on('hostname -> client.hostname,
              'port -> client.port,
              'password -> client.password,
              'encoding -> client.encoding,
              'delay -> client.delay,
              'nickanme -> client.nickname,
              'username -> client.username,
              'realname -> client.realname).executeInsert()
      }
  }
}
