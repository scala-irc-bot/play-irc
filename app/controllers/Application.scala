package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import net.mtgto.domain.{User, UserRepository, Client, ClientRepository, Channel, ChannelRepository, Bot, BotRepository, IrcBot}

import scalaz.Identity

object Application extends Controller with Secured {
  protected[this] val userRepository: UserRepository = UserRepository()

  protected[this] val clientRepository: ClientRepository = ClientRepository()

  protected[this] val channelRepository: ChannelRepository = ChannelRepository()

  protected[this] val botRepository: BotRepository = BotRepository()

  protected[this] val loginForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "password" -> text
    ) verifying ("ユーザ名かパスワードが違います", result => result match {
      case (name, password) => userRepository.findByNameAndPassword(name, password).isDefined
      case _ => false
    })
  )

  val ircBot: IrcBot = IrcBot()

  def index = IsAuthenticated { user => implicit request =>
    val client: Option[Client] = clientRepository.findHead
    val channels: Seq[Channel] = channelRepository.findAll
    val bots: Seq[Bot] = botRepository.findAll
    Ok(views.html.index("ようこそ、" + user.name + " さん", client, channels, bots))
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
