package controllers.todo

import javax.inject._
import play.api.mvc._

import model.ViewValueHome
import lib.model.Todo

@Singleton
class TodoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
    val todo_list_sample: Seq[Todo] = (0 to 10).map(i => Todo(s"todo $i"))

    def list() = Action { implicit request => 
        val vv = ViewValueHome(
        title  = "Todo一覧",
        cssSrc = Seq("main.css"),
        jsSrc  = Seq("main.js")
        )
        Ok(views.html.todo.List(vv, todo_list_sample))
    }
}
