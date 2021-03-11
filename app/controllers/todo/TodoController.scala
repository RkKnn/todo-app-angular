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
import ixias.model.Entity

@Singleton
class TodoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
    val vv = ViewValueHome(
        title  = "Todo一覧",
        cssSrc = Seq("main.css"),
        jsSrc  = Seq("main.js")
    )

    def list() = Action.async { implicit request => 
        TodoRepository().getAll.map { value => 
            val todo_list_vv = ViewValueList(vv, RegisterFormData.registerForm, DeleteFormData.deleteForm, value.map(_.v))
            Ok(views.html.todo.List(todo_list_vv))
        }
    }

    def register() = Action.async { implicit request =>
        RegisterFormData.registerForm.bindFromRequest().fold (
            (formWithErrors: Form[RegisterFormData]) => {
                TodoRepository().getAll.map { value => 
                    val todo_list_vv = ViewValueList(vv, formWithErrors, DeleteFormData.deleteForm, value.map(_.v))
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

    def delete() = Action.async { implicit request =>
        DeleteFormData.deleteForm.bindFromRequest().fold (
            (formWithErrors: Form[DeleteFormData]) => Future.successful(Redirect("/todo")),
            (formData: DeleteFormData) => {
                println(formData.ids)
                TodoRepository().removeAll(formData.ids.map(Todo.Id(_))).map { _ =>
                    Redirect("/todo")
                }
            }
        )
    }
}
