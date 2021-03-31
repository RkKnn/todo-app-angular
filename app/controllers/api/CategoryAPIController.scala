package controllers.api

import play.api.libs.json.Json
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import javax.inject._
import lib.persistence.default._
import controllers.json.{ CategoryJson }
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import lib.model.Category

@Singleton
class CategoryAPIController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def categories = Action.async { implicit request =>
    for {
      categories: Seq[Category.EmbeddedId] <- CategoryRepository.getAll
    } yield {
      val categoriesJson = categories.map(CategoryJson.createCategoryJson)
      Ok(Json.toJson(categoriesJson))
    }
  }
}
