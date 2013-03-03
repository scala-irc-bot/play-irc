package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import util.{Try, Success, Failure}

import java.io.File
import java.util.UUID

import net.mtgto.domain.{Bot, BotRepository, UserRepository, BotFactory}

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

  protected[this] val editForm = Form(
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
          case (Some(file), (name, config)) if file.filename.length > 0 => {
            val filename = file.filename
            // 先に移動先に同盟のファイルが有るかどうかを確認する（いまは同名ファイルがあれば失敗する）
            val bot = BotFactory(name, filename, Option(config).filter(_.nonEmpty), true)
            Logger.info("moving uploaded file " + filename + " to " + new File("bots", filename))
            file.ref.moveTo(new File("bots", filename))
            botRepository.store(bot)
            Redirect(routes.Application.index).flashing(
              "success" -> ("ボットを追加しました"))
          }
          case (_, _) => {
            Redirect(net.mtgto.controllers.routes.BotController.create).flashing("error" -> "ファイルを指定してください")
          }
        }
      }
    )
  }

  def showEditView(id: String) = IsAuthenticated { user => implicit request =>
    getBotByIdString(id) match {
      case Some(bot) =>
        Ok(views.html.bots.edit(id, editForm.fill((bot.name, bot.config.getOrElse("")))))
      case _ =>
        Redirect(routes.Application.index).flashing("error" -> "編集しようとしたボットが見つかりません")
    }
  }

  def edit(id: String) = IsAuthenticated(parse.multipartFormData) { user => implicit request =>
    editForm.bindFromRequest.fold(
      formWithErrors =>
        BadRequest(views.html.bots.edit(id, formWithErrors)).flashing("error" -> "入力項目にエラーがあります"),
      nameAndConfig => {
        getBotByIdString(id) match {
          case Some(bot) => {
            nameAndConfig match {
              case (name, config) => {
                request.body.file("file") match {
                  case Some(file) if file.filename.length > 0 => {
                    new File("bots", bot.filename).delete
                    val filename = file.filename
                    // 先に移動先に同盟のファイルが有るかどうかを確認する（いまは同名ファイルがあれば失敗する）
                    Logger.info("moving uploaded file " + filename + " to " + new File("bots", filename))
                    file.ref.moveTo(new File("bots", filename))
                    botRepository.store(Bot(bot.identity, name, filename, Option(config).filter(_.nonEmpty), bot.enabled))
                  }
                  case _ => {
                    botRepository.store(Bot(bot.identity, name, bot.filename, Option(config).filter(_.nonEmpty), bot.enabled))
                  }
                }
                Redirect(routes.Application.index).flashing(
                  "success" -> "ボットの情報を更新しました")
              }
            }
          }
          case _ =>
            Redirect(routes.Application.index).flashing("error" -> "編集しようとしたボットが見つかりません")
        }
      }
    )
  }

  def enable(id: String) = IsAuthenticated { user => implicit request =>
    getBotByIdString(id) match {
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

  def disable(id: String) = IsAuthenticated { user => implicit request =>
    getBotByIdString(id) match {
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

  protected[this] def getBotByIdString(id: String): Option[Bot] = {
    Try(UUID.fromString(id)) match {
      case Success(uuid) =>
        botRepository.resolveOption(Identity(uuid))
      case Failure(e) =>
        None
    }
  }
}
