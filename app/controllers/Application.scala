package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import net.mtgto.domain.{User, UserRepository}

import scalaz.Identity

object Application extends Controller with Secured {
  protected[this] val userRepository: UserRepository = new UserRepository {
    private def getDummyUser(identifier: Identity[Int]): User = {
      new User {
        override val identity = identifier
        override val name = "admin"
      }
    }
    def findByNameAndPassword(name: String, password: String): Option[User] = {
      (name, password) match {
        case ("admin", "admin") => Some(getDummyUser(Identity(1)))
        case _ => None
      }
    }

    override def resolve(identifier: Identity[Int]): User = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[Int]): Option[User] = {
      identifier.value match {
        case 1 => Some(getDummyUser(Identity(1)))
        case _ => None
      }
    }

    override def contains(identifier: Identity[Int]): Boolean = {
      identifier.value == 1
    }

    override def contains(entity: User): Boolean = {
      entity.identity.value == 1
    }
  }

  protected[this] val loginForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "password" -> text
    ) verifying ("ユーザ名かパスワードが違います", result => result match {
      case (name, password) => userRepository.findByNameAndPassword(name, password).isDefined
      case _ => false
    })
  )

  def index = IsAuthenticated { user => implicit request =>
    Ok(views.html.index("ようこそ、" + user.name + " さん"))
  }

  def login = Action {
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
          val user: User = userNameAndPassword match {
            case (name, password) =>
              userRepository.findByNameAndPassword(name, password).get
          }
          Redirect(routes.Application.login).withSession("userId" -> user.identity.value.toString)
        }
      )
  }
}

trait Secured {
  protected val userRepository: UserRepository

  protected def getUser(request: RequestHeader): Option[User] = {
    request.session.get("userId").flatMap( userId =>
      userRepository.resolveOption(Identity(userId.toInt)))
  }

  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)

  def IsAuthenticated(f: => User => Request[AnyContent] => Result) = Security.Authenticated(getUser, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}
