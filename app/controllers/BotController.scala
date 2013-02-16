package net.mtgto.controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import net.mtgto.domain.UserRepository

object BotController extends Controller with Secured {
  protected[this] val userRepository: UserRepository = UserRepository()

  protected[this] val createForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "config" -> text
    )
  )

  def showCreateView = IsAuthenticated { user => implicit request =>
    Ok(views.html.bots.create(createForm))
  }

  def create = Action(parse.multipartFormData) { implicit request =>
    createForm.bindFromRequest.fold(
        formWithErrors =>
          BadRequest(views.html.bots.create(formWithErrors)).flashing("error" -> "入力項目にエラーがあります"),
        nameAndConfig => {
          (request.body.file("file"), nameAndConfig) match {
            case (Some(file), (name, config)) => {
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
}
