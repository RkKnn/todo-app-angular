package controllers.todo

import javax.inject._
import play.api.mvc._

import model.ViewValueHome
import model.todo.ViewValueList
import lib.model.Todo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import lib.persistence.onMySQL.driver
import lib.persistence.TodoRepository
import scala.util.Success
import scala.util.Failure

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._

case class RegisterFormData (title: String, body: String)

@Singleton
class TodoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
    val registerForm = Form(
        mapping(
            "title" -> nonEmptyText(maxLength = 255),
            "body" -> nonEmptyText()
        )(RegisterFormData.apply)(RegisterFormData.unapply)
    )

    val vv = ViewValueHome(
        title  = "Todo一覧",
        cssSrc = Seq("main.css"),
        jsSrc  = Seq("main.js")
    )

    def list() = Action.async { implicit request => 
        TodoRepository().getAll.map { value => 
            val todo_list_vv = ViewValueList(vv, registerForm, value.map(_.v))
            Ok(views.html.todo.List(todo_list_vv))
        }
    }

    def register() = Action.async { implicit request =>
        registerForm.bindFromRequest().fold (
            (formWithErrors: Form[RegisterFormData]) => {
                TodoRepository().getAll.map { value => 
                    val todo_list_vv = ViewValueList(vv, formWithErrors, value.map(_.v))
                    // Ok(views.html.todo.List(todo_list_vv))
                    BadRequest(views.html.todo.List(todo_list_vv))
                }
            },
            (formData: RegisterFormData) => {
                val todo = Todo(0, formData.title, formData.body, 0)
                TodoRepository().add(todo).map { _ =>
                    Redirect("/todo")
                }
            }
        )
    }

    def remove() = Action.async { implicit request =>
    }
}
