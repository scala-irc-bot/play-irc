package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import java.io.File

import net.mtgto.domain.{BotRepository, UserRepository, BotFactory}

import scalaz.Identity

object BotController extends Controller with Secured {
  protected[this] val userRepository: UserRepository = UserRepository()

  protected[this] val botRepository: BotRepository = BotRepository()

  protected[this] val createForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "config" -> text
    )
  )

  def showCreateView = IsAuthenticated { user => implicit request =>
    Ok(views.html.bots.create(createForm))
  }

  def create = IsAuthenticated(parse.multipartFormData) { user => implicit request =>
    createForm.bindFromRequest.fold(
        formWithErrors =>
          BadRequest(views.html.bots.create(formWithErrors)).flashing("error" -> "入力項目にエラーがあります"),
        nameAndConfig => {
          (request.body.file("file"), nameAndConfig) match {
            case (Some(file), (name, config)) => {
              val filename = file.filename
              // 先に移動先に同盟のファイルが有るかどうかを確認する（いまは同名ファイルがあれば失敗する）
              val bot = BotFactory(name, filename, Option(config).filter(_.nonEmpty), true)
              Logger.info("moving uploaded file to " + new File("bots", filename))
              file.ref.moveTo(new File("bots", filename))
              Redirect(routes.Application.index).flashing(
                "success" -> ("name = " + name + ", config = " + config + ", file = " + request.body.file("file")))
            }
            case (None, _) => {
              Redirect(net.mtgto.controllers.routes.BotController.create).flashing("error" -> "ファイルを指定してください")
            }
          }
        }
    )
  }

  def enable(id: Int) = IsAuthenticated { user => implicit request =>
    botRepository.resolveOption(Identity(id)) match {
      case Some(bot) if !bot.enabled => {
        botRepository.store(bot.toEnable)
        Redirect(routes.Application.index).flashing("success" -> "ボットを有効にしました")
      }
      case Some(bot) =>
        Redirect(routes.Application.index).flashing("error" -> "有効にしようとしたボットはすでに有効になっています")
      case _ =>
        Redirect(routes.Application.index).flashing("error" -> "有効にしようとしたボットが見つかりません")
    }
  }

  def disable(id: Int) = IsAuthenticated { user => implicit request =>
    botRepository.resolveOption(Identity(id)) match {
      case Some(bot) if bot.enabled => {
        botRepository.store(bot.toDisable)
        Redirect(routes.Application.index).flashing("success" -> "ボットを無効にしました")
      }
      case Some(bot) =>
        Redirect(routes.Application.index).flashing("error" -> "無効にしようとしたボットはすでに無効になっています")
      case _ =>
        Redirect(routes.Application.index).flashing("error" -> "無効にしようとしたボットが見つかりません")
    }
  }
}
