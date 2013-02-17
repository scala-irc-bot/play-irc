package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import net.mtgto.domain.{User, UserRepository, ChannelFactory}

import scalaz.Identity

object ChannelController extends Controller with Secured {
  protected[this] val userRepository: UserRepository = UserRepository()

  protected[this] val createForm = Form(
    "name" -> nonEmptyText
  )

  def create = IsAuthenticated { user => implicit request =>
    createForm.bindFromRequest.fold(
      formWithErrors =>
        Redirect(routes.Application.index).flashing("error" -> ("入力項目にエラーがあります" + formWithErrors)),
      success => {
        success match {
          case (name) =>
            val channel = ChannelFactory(name)
            Redirect(routes.Application.index).flashing("success" -> (name + "を追加したよ"))
        }
      }
    )
  }
}
