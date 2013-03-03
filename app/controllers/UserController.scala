package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import net.mtgto.domain.{User, UserRepository, UserFactory}

import scalaz.Identity

object UserController extends Controller with Secured {
  protected[this] val userRepository: UserRepository = UserRepository()

  private def getConfiguration(name: String): String = Play.current.configuration.getString(name).get

  private val (adminName, adminPassword) = (getConfiguration("irc.admin_name"), getConfiguration("irc.admin_password"))

  private val passwordHashingSalt = getConfiguration("irc.password_salt")

  protected[this] val loginForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "password" -> text
    )
  )

  protected[this] val createForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "password" -> text
    )
  )

  protected[this] val changePasswordForm = Form(
    tuple(
      "oldPassword" -> text,
      "newPassword" -> text
    )
  )

  def login = Action { implicit request =>
    Ok(views.html.users.login(loginForm))
  }

  def logout = IsAuthenticated { user => implicit request =>
    Redirect(routes.Application.index).withNewSession
  }

  def authenticate = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors =>
          BadRequest(views.html.users.login(formWithErrors)),
        userNameAndPassword => {
          val user: Option[User] = userNameAndPassword match {
            case (name, password) => {
              val hashedPassword = getHashedPassword(name, password)
              userRepository.findByNameAndPassword(name, hashedPassword).orElse {
                if (name == adminName && password == adminPassword) {
                  val adminUser = UserFactory.createUser(name, hashedPassword)
                  userRepository.store(adminUser)
                  userRepository.findByNameAndPassword(name, hashedPassword)
                } else {
                  None
                }
              }
            }
          }
          user match {
            case Some(user) => 
              Redirect(routes.Application.index).withSession("userId" -> user.identity.value.toString)
            case None =>
              Redirect(routes.Application.index).flashing("error" -> "名前かパスワードが違います")
          }
        }
      )
  }

  def create = IsAuthenticated { user => implicit request =>
    createForm.bindFromRequest.fold(
      formWithErrors =>
        Redirect(routes.Application.index).flashing("error" -> ("入力項目にエラーがあります" + formWithErrors)),
      success => success match {
        case (name, password) =>
          userRepository.store(UserFactory.createUser(name, getHashedPassword(name, password)))
          Redirect(routes.Application.index).flashing("success" -> ("ユーザ " + name + " を作成しました"))
      }
    )
  }

  def changePassword = IsAuthenticated { user => implicit request =>
    changePasswordForm.bindFromRequest.fold(
      formWithErrors =>
        Redirect(routes.Application.index).flashing("error" -> ("入力項目にエラーがあります" + formWithErrors)),
      success => success match {
        case (oldPassword, newPassword) =>
          val oldHashedPassword = getHashedPassword(user.name, oldPassword)
          userRepository.findByNameAndPassword(user.name, oldHashedPassword) match {
            case Some(user) =>
              userRepository.store(User(user.identity, user.name, Some(getHashedPassword(user.name, newPassword))))
              Redirect(routes.Application.index).flashing("success" -> "パスワードを変更しました")
            case _ =>
              Redirect(routes.Application.index).flashing("error" -> "パスワードが違うよ")
          }
      }
    )
  }

  protected[this] def getHashedPassword(name: String, password: String): String = {
    val passwordBytes = (name + passwordHashingSalt + password).getBytes("UTF-8")
    toHashedString(passwordBytes)
  }

  protected[this] def toHashedString(bytes: Array[Byte]): String = {
    import java.security.MessageDigest
    val md = MessageDigest.getInstance("SHA-256")
    md.update(bytes)
    md.digest.map("%02x".format(_)).mkString
  }
}
