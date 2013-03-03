package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import net.mtgto.domain.{User, UserRepository, Client, ClientRepository, Channel, ChannelRepository, Bot, BotRepository, IrcBot}

import util.{Try, Success, Failure}
import scalaz.Identity
import java.util.UUID

object Application extends Controller with Secured {
  protected[this] val userRepository: UserRepository = UserRepository()

  protected[this] val clientRepository: ClientRepository = ClientRepository()

  protected[this] val channelRepository: ChannelRepository = ChannelRepository()

  protected[this] val botRepository: BotRepository = BotRepository()

  val ircBot: IrcBot = IrcBot()

  def index = IsAuthenticated { user => implicit request =>
    val client: Option[Client] = clientRepository.findHead
    val channels: Seq[Channel] = channelRepository.findAll
    val bots: Seq[Bot] = botRepository.findAll
    val users: Seq[User] = userRepository.findAll
    Ok(views.html.index("ようこそ、" + user.name + " さん", client, ircBot.isConnected, channels, bots, users))
  }

  def connect = IsAuthenticated { user => implicit request =>
    val client: Option[Client] = clientRepository.findHead
    val channels: Seq[Channel] = channelRepository.findAll
    val bots: Seq[Bot] = botRepository.findAll
    (ircBot.isConnected, client) match {
      case (false, Some(client)) =>
        ircBot.connect(client, channels, bots)
        Redirect(routes.Application.index).flashing(
          "success" -> "接続したよ"
        )
      case (true, _) =>
        Redirect(routes.Application.index).flashing(
          "error" -> "すでに接続してるよ"
        )
      case _ =>
        Redirect(routes.Application.index).flashing(
          "error" -> "クライアントの設定をしてないよ"
        )
    }
  }

  def disconnect = IsAuthenticated { user => implicit request =>
    if (ircBot.isConnected) {
      ircBot.disconnect
      Redirect(routes.Application.index).flashing(
        "success" -> "接続きったよ"
      )
    } else {
      Redirect(routes.Application.index).flashing(
        "error" -> "接続してないよ"
      )
    }
  }
}

trait Secured {
  protected val userRepository: UserRepository

  protected def getUser(request: RequestHeader): Option[User] = {
    request.session.get("userId").flatMap( userId =>
      Try(UUID.fromString(userId)) match {
        case Success(id) => userRepository.resolveOption(Identity(id))
        case Failure(e) => None
      }
    )
  }

  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.UserController.login)

  def IsAuthenticated(f: => User => Request[AnyContent] => Result) = Security.Authenticated(getUser, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  def IsAuthenticated[A](bodyParser: BodyParser[A])(f: => User => Request[A] => Result) = Security.Authenticated(getUser, onUnauthorized) { user =>
    Action(bodyParser)(request => f(user)(request))
  }
}
