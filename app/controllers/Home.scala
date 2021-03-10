/**
 *
 * to do sample project
 *
 */

package controllers

import javax.inject._
import play.api.mvc._

import model.ViewValueHome
import lib.persistence.TodoRepository
import scala.concurrent.ExecutionContext.Implicits.global
import lib.persistence.onMySQL.driver
import scala.util.Success
import scala.util.Failure

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index() = Action { implicit req =>
    val data = TodoRepository().get(lib.model.Todo.Id(1))
    data.onComplete {
      case Success(value) => println(value)
      case Failure(exception) => println(s"error: $exception")
    }

    val vv = ViewValueHome(
      title  = "Todoアプリ",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js")
    )
    Ok(views.html.Home(vv))
  }
}
