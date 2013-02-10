package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import net.mtgto.domain.UserRepository

class UserController {
  case class FormUser(name: String, password: String)
}

object UserController extends Controller {
  protected[this] val userRepository: UserRepository = new UserRepository

  protected[this] val loginForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "password" -> text
    ) verifying ("ユーザ名かパスワードが違います", result => result match {
      case (name, password) => userRepository.findByNameAndPassword(name, password).isDefined
    })
  )

  def login = Action {
    Ok(views.html.users.login(loginForm))
  }
}
