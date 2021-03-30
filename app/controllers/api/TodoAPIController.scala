package controllers.api

import play.api.libs.json.Json
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import javax.inject._
import lib.persistence.default._
import lib.model.Todo
import controllers.json.TodoWrites

@Singleton
class TodoAPIController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def todos = Action.async { implicit request =>
    for {
      todoList: Seq[Todo.EmbeddedId] <- TodoRepository.getAll
    } yield {
      val todosJson = Seq(todoList.map(TodoWrites.createTodoWrites))
      Ok(Json.toJson(todosJson))
    }
  }
}
