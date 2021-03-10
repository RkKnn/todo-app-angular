package controllers.todo

import javax.inject._
import play.api.mvc._

import model.ViewValueHome
import lib.model.Todo

import scala.concurrent.ExecutionContext.Implicits.global
import lib.persistence.onMySQL.driver
import lib.persistence.TodoRepository
import scala.util.Success
import scala.util.Failure

@Singleton
class TodoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
    def list() = Action.async { implicit request => 
        TodoRepository().getAll.map { value => 
            val vv = ViewValueHome(
            title  = "Todo一覧",
            cssSrc = Seq("main.css"),
            jsSrc  = Seq("main.js")
            )
            Ok(views.html.todo.List(vv, value.map(_.v)))
        }
    }
}
