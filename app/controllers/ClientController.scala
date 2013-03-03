package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import net.mtgto.domain.{User, UserRepository, Client, ClientFactory, ClientRepository}

import util.{Try, Success, Failure}

import scalaz.Identity
import java.util.UUID

object ClientController extends Controller with Secured {
  protected[this] val userRepository: UserRepository = UserRepository()

  protected[this] val clientRepository: ClientRepository = ClientRepository()

  protected[this] val createForm = Form(
    tuple(
      "hostname" -> nonEmptyText,
      "port" -> number(1, 65535),
      "password" -> optional(text),
      "encoding" -> nonEmptyText,
      "messageDelay" -> number,
      "timerDelay" -> number,
      "nickname" -> nonEmptyText,
      "username" -> nonEmptyText,
      "realname" -> nonEmptyText
    )
  )

  protected[this] val editForm = Form(
    tuple(
      "hostname" -> nonEmptyText,
      "port" -> number(1, 65535),
      "password" -> optional(text),
      "encoding" -> nonEmptyText,
      "messageDelay" -> number,
      "timerDelay" -> number,
      "nickname" -> nonEmptyText,
      "username" -> nonEmptyText,
      "realname" -> nonEmptyText
    )
  )

  def showCreateView = IsAuthenticated { user => implicit request =>
    Ok(views.html.clients.create(createForm))
  }

  def create = IsAuthenticated { user => implicit request =>
    createForm.bindFromRequest.fold(
      formWithErrors =>
        Redirect(routes.Application.index).flashing("error" -> ("入力項目にエラーがあります" + formWithErrors)),
      success => {
        success match {
          case (hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname) =>
            val client = ClientFactory(hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname)
            clientRepository.store(client)
            Redirect(routes.Application.index).flashing("success" -> "クライアントを設定したよ")
        }
      }
    )
  }

  def showEditView(id: String) = IsAuthenticated { user => implicit request =>
    getClientByIdString(id) match {
      case Some(client) =>
        Ok(views.html.clients.edit(id, editForm.fill(
          (client.hostname, client.port, client.password, client.encoding, client.messageDelay, client.timerDelay,
           client.nickname, client.username, client.realname))))
      case _ =>
        Redirect(routes.Application.index).flashing("error" -> "編集しようとしたクライアントが見つかりません")
    }
  }

  def edit(id: String) = IsAuthenticated { user => implicit request =>
    editForm.bindFromRequest.fold(
      formWithErrors =>
        BadRequest(views.html.clients.edit(id, formWithErrors)).flashing("error" -> "入力項目にエラーがあります"),
      success => {
        getClientByIdString(id) match {
          case Some(client) => {
            success match {
              case (hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname) =>
                clientRepository.store(
                  Client(client.identity, hostname, port, password, encoding, messageDelay, timerDelay, nickname, username, realname))
                Redirect(routes.Application.index).flashing("success" -> "クライアントを設定したよ")
            }
          }
          case _ =>
            Redirect(routes.Application.index).flashing("error" -> "編集しようとしたクライアントが見つかりません")
        }
      }
    )
  }

  protected[this] def getClientByIdString(id: String): Option[Client] = {
    Try(UUID.fromString(id)) match {
      case Success(uuid) =>
        clientRepository.resolveOption(Identity(uuid))
      case Failure(e) =>
        None
    }
  }
}
