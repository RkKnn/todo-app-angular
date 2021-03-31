package controllers.api

import play.api.libs.json.Json
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import javax.inject._
import lib.persistence.default._
import lib.model.Todo
import controllers.json.{ TodoJson, RegisterJson, CategoryJson }
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import lib.model.Category

@Singleton
class TodoAPIController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def todos = Action.async { implicit request =>
    for {
      todoList: Seq[Todo.EmbeddedId] <- TodoRepository.getAll
    } yield {
      val todosJson = todoList.map(TodoJson.createTodoJson)
      Ok(Json.toJson(todosJson))
    }
  }

  def categoryRef = Action.async { implicit request =>
    val json = for {
      json <- request.body.asJson
    } yield json.validate[Seq[Int]]

    json match {
      case Some(JsSuccess(value, path)) => {
        for {
          categoryRef <- TodoRepository.createCategoryRef(value.map(Category.Id(_)))
          categoryRefJson: Map[Long, CategoryJson] = categoryRef.transform((key, value) => CategoryJson.createCategoryJson(value))
        } yield {
          Ok(Json.toJson(categoryRefJson))
        }
      }
      case _ => Future.successful((InternalServerError))
    }
  }

  def register = Action.async { implicit request =>
    val json = for {
      json <- request.body.asJson
    } yield json.validate[RegisterJson]

    json match {
      case Some(JsSuccess(value, path)) => {
        val todo = Todo(Category.Id(value.categoryId), value.title, value.body, Todo.StateType.withName(value.state))
        for {
          _ <- TodoRepository.add(todo)
        } yield Ok
      }
      case _ => Future.successful(InternalServerError)
    }
  }

  def delete = Action.async { implicit request =>
    val json = for {
      json <- request.body.asJson
    } yield json.validate[Seq[Int]]

    json match {
      case Some(JsSuccess(value, path)) => {
        for {
          _ <- TodoRepository.removeAll(value.map(Todo.Id(_)))
        } yield Ok
      }
      case _ => Future.successful(InternalServerError)
    }
  }
}
