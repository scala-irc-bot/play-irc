package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import net.mtgto.domain.{User, UserRepository, Client, ClientRepository, Channel, ChannelRepository, Bot, BotRepository}
import net.mtgto.infrastracture.{UserDao, DatabaseUserDao, ClientDao, DatabaseClientDao, ChannelDao, DatabaseChannelDao, BotDao, DatabaseBotDao}

import scalaz.Identity

object Application extends Controller with Secured {
  protected[this] val userRepository: UserRepository = new UserRepository {
    private val userDao: UserDao = new DatabaseUserDao

    def findByNameAndPassword(name: String, password: String): Option[User] = {
      userDao.findByNameAndPassword(name, password).map {
        infraUser => User(Identity(infraUser.id), infraUser.name)
      }
    }

    override def resolve(identifier: Identity[Int]): User = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[Int]): Option[User] = {
      userDao.findById(identifier.value).map {
        infraUser => User(Identity(infraUser.id), infraUser.name)
      }
    }

    override def contains(identifier: Identity[Int]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: User): Boolean = {
      resolveOption(entity.identity).isDefined
    }
  }

  protected[this] val clientRepository: ClientRepository = new ClientRepository {
    import net.mtgto.infrastracture.{Client => InfraClient}

    private val clientDao: ClientDao = new DatabaseClientDao

    private def convertInfraToDomain(infraClient: InfraClient): Client = {
      Client(Identity(infraClient.id.get.toInt),
             infraClient.hostname,
             infraClient.port,
             infraClient.password,
             infraClient.encoding,
             infraClient.delay,
             infraClient.nickname,
             infraClient.username,
             infraClient.realname)
    }

    override def resolve(identifier: Identity[Int]): Client = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[Int]): Option[Client] = {
      clientDao.findById(identifier.value).map(convertInfraToDomain)
    }

    override def contains(identifier: Identity[Int]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: Client): Boolean = {
      resolveOption(entity.identity).isDefined
    }

    override def findHead: Option[Client] = {
      clientDao.findAll.headOption.map(convertInfraToDomain)
    }
  }

  protected[this] val channelRepository: ChannelRepository = new ChannelRepository {
    private val channelDao: ChannelDao = new DatabaseChannelDao

    def findAll: Seq[Channel] = {
      channelDao.findAll.map {
        infraChannel => Channel(Identity(infraChannel.id), infraChannel.name)
      }
    }

    override def resolve(identifier: Identity[Int]): Channel = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[Int]): Option[Channel] = {
      channelDao.findById(identifier.value).map {
        infraChannel => Channel(Identity(infraChannel.id), infraChannel.name)
      }
    }

    override def contains(identifier: Identity[Int]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: Channel): Boolean = {
      resolveOption(entity.identity).isDefined
    }
  }

  protected[this] val botRepository: BotRepository = new BotRepository {
    private val botDao: BotDao = new DatabaseBotDao

    def findAll: Seq[Bot] = {
      botDao.findAll.map {
        infraBot => Bot(Identity(infraBot.id), infraBot.name, infraBot.filename, infraBot.config, infraBot.enabled)
      }
    }

    override def resolve(identifier: Identity[Int]): Bot = {
      resolveOption(identifier).get
    }

    override def resolveOption(identifier: Identity[Int]): Option[Bot] = {
      botDao.findById(identifier.value).map {
        infraBot => Bot(Identity(infraBot.id), infraBot.name, infraBot.filename, infraBot.config, infraBot.enabled)
      }
    }

    override def contains(identifier: Identity[Int]): Boolean = {
      resolveOption(identifier).isDefined
    }

    override def contains(entity: Bot): Boolean = {
      resolveOption(entity.identity).isDefined
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
