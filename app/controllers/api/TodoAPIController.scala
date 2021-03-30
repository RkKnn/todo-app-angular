package controllers.api

import play.api.libs.json.Json
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import javax.inject._
import lib.persistence.onMySQL.driver

@Singleton
class TodoAPIController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def todos = Action.async { implicit request =>
    
    Future.successful(
      Ok(Json.obj("status" -> "ok"))
    )
  }
}
