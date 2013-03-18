package net.mtgto.infrastructure

import anorm.Row
import java.util.UUID

import anorm._
import anorm.SqlParser._
import play.api.db.DB

trait ClientDao {
  def findById(id: UUID): Option[Client]

  def findAll: Seq[Client]

  def save(client: Client): Unit

  /**
   * return the number of deleted rows
   */
  def delete(id: UUID): Int
}

class DatabaseClientDao extends ClientDao {
  import play.api.Play.current

  protected[this] def convertRowToClient(row: Row): Client = {
    row match {
      case Row(id: String, hostname: String, port: Int, Some(password: String), encoding: String, messageDelay: Int, timerDelay: Int, nickname: String, username: String, realname: String) =>
        Client(UUID.fromString(id), hostname, port, Some(password), encoding, messageDelay, timerDelay, nickname, username, realname)
      case Row(id: String, hostname: String, port: Int, None, encoding: String, messageDelay: Int, timerDelay: Int, nickname: String, username: String, realname: String) =>
        Client(UUID.fromString(id), hostname, port, None, encoding, messageDelay, timerDelay, nickname, username, realname)
    }
  }
  override def findById(id: UUID): Option[Client] = {
    DB.withConnection{ implicit c =>
      SQL("SELECT `id`,`hostname`,`port`,`password`,`encoding`,`message_delay`,`timer_delay`,`nickname`,`username`,`realname` FROM `clients` WHERE `id` = {id}")
                      .on("id" -> id)().headOption.map(convertRowToClient)
    }
  }

  override def findAll: Seq[Client] = {
    DB.withConnection{ implicit c =>
      SQL("SELECT `id`,`hostname`,`port`,`password`,`encoding`,`message_delay`,`timer_delay`,`nickname`,`username`,`realname` FROM `clients`")().map(convertRowToClient)
    }
  }

  override def save(client: Client): Unit = {
    DB.withConnection{ implicit c =>
      val rowCount =
        SQL("""
          UPDATE `clients` SET
          `hostname` = {hostname},
          `port` = {port},
          `password` = {password},
          `encoding` = {encoding},
          `message_delay` = {message_delay},
          `timer_delay` = {timer_delay},
          `nickname` = {nickname},
          `username` = {username},
          `realname` = {realname} WHERE `id` = {id}
          """)
          .on('id -> client.id,
              'hostname -> client.hostname,
              'port -> client.port,
              'password -> client.password,
              'encoding -> client.encoding,
              'message_delay -> client.messageDelay,
              'timer_delay -> client.timerDelay,
              'nickname -> client.nickname,
              'username -> client.username,
              'realname -> client.realname).executeUpdate()
      if (rowCount == 0)
        SQL("""
          INSERT INTO `clients` (`id`, `hostname`,`port`,`password`,`encoding`,`message_delay`,`timer_delay`,`nickname`,`username`,`realname`)
          VALUES ({id}, {hostname}, {port}, {password}, {encoding}, {message_delay}, {timer_delay}, {nickname}, {username}, {realname})
          """)
          .on('id -> client.id,
              'hostname -> client.hostname,
              'port -> client.port,
              'password -> client.password,
              'encoding -> client.encoding,
              'message_delay -> client.messageDelay,
              'timer_delay -> client.timerDelay,
              'nickname -> client.nickname,
              'username -> client.username,
              'realname -> client.realname).executeInsert()
      }
  }

  override def delete(id: UUID): Int = {
    DB.withConnection{ implicit c =>
      SQL("DELETE `clients` WHERE `id` = {id}")
        .on('id -> id).executeUpdate()
    }
  }
}
