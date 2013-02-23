package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import net.mtgto.domain.{User, UserRepository, ClientFactory}

import scalaz.Identity

object ClientController extends Controller with Secured {
  protected[this] val userRepository: UserRepository = UserRepository()

  protected[this] val createForm = Form(
    tuple(
      "hostname" -> nonEmptyText,
      "port" -> number(1, 65535),
      "password" -> optional(text),
      "encoding" -> nonEmptyText,
      "delay" -> number,
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
          case (hostname, port, password, encoding, delay, nickname, username, realname) =>
            val client = ClientFactory(hostname, port, password, encoding, delay, nickname, username, realname)
            Redirect(routes.Application.index).flashing("success" -> "クライアントを設定したよ")
        }
      }
    )
  }
}