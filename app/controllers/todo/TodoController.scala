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
import lib.persistence.db.StateType
import lib.model.Category
import lib.persistence.CategoryRepository

@Singleton
class TodoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
    val vv = ViewValueHome(
        title  = "Todo一覧",
        cssSrc = Seq("main.css"),
        jsSrc  = Seq("main.js")
    )

    def listPage() = Action.async { implicit request => 
        for {
            todo <- TodoRepository().getAll
            category <- CategoryRepository().getAll
        } yield {
            val todoListVV = ViewValueList(
                vv,
                RegisterFormData.registerForm, SelectIdFormData.selectIdForm,
                todo.map(_.v), Todo.createCategoryRef(todo, category))
            Ok(views.html.todo.List(todoListVV))
        }
        // TodoRepository().getAll.map { value => 
        //     val todoListVV = ViewValueList(vv, RegisterFormData.registerForm, SelectIdFormData.selectIdForm, value.map(_.v))
        //     Ok(views.html.todo.List(todoListVV))
        // }
    }

    def trushPage() = Action.async { implicit request => 
        for {
            todo <- TodoRepository().getAll
            category <- CategoryRepository().getAll
        } yield {
            val todoListVV = ViewValueList(
                vv.copy(title = "ゴミ箱"),
                RegisterFormData.registerForm, SelectIdFormData.selectIdForm,
                todo.map(_.v), Todo.createCategoryRef(todo, category))
            Ok(views.html.todo.Trush(todoListVV))
        }
        // TodoRepository().getAll.map { value => 
        //     val todoListVV = ViewValueList(vv.copy(title = "ゴミ箱"), RegisterFormData.registerForm, SelectIdFormData.selectIdForm, value.map(_.v))
        //     Ok(views.html.todo.Trush(todoListVV))
        // }
    }

    def register() = Action.async { implicit request =>
        RegisterFormData.registerForm.bindFromRequest().fold (
            (formWithErrors: Form[RegisterFormData]) => {
                for {
                    todo <- TodoRepository().getAll
                    category <- CategoryRepository().getAll
                } yield { 
                    val todoListVV = ViewValueList(
                        vv,
                        formWithErrors, SelectIdFormData.selectIdForm,
                        todo.map(_.v), Todo.createCategoryRef(todo, category))
                    // Ok(views.html.todo.List(todoListVV))
                    BadRequest(views.html.todo.List(todoListVV))
                }
                // TodoRepository().getAll.map { value => 
                //     val todoListVV = ViewValueList(vv, formWithErrors, SelectIdFormData.selectIdForm, value.map(_.v))
                //     // Ok(views.html.todo.List(todoListVV))
                //     BadRequest(views.html.todo.List(todoListVV))
                // }
            },
            (formData: RegisterFormData) => {
                val todo = Todo(Category.Id(0), formData.title, formData.body, Todo.StateT.ACTIVE)
                TodoRepository().add(todo).map { _ =>
                    Redirect(controllers.todo.routes.TodoController.listPage())
                }
                // TodoRepository().add(todo).map { _ =>
                //     Redirect(controllers.todo.routes.TodoController.listPage())
                // }
            }
        )
    }

    private def trush(success: SelectIdFormData => Future[_]) = Action.async { implicit request =>
        SelectIdFormData.selectIdForm.bindFromRequest().fold (
            (formWithErrors: Form[SelectIdFormData]) => Future.successful(Redirect(controllers.todo.routes.TodoController.trushPage())),
            (formData: SelectIdFormData) => {
                for {
                    _ <- success(formData)
                } yield {
                    Redirect(controllers.todo.routes.TodoController.trushPage())
                }
                // success(formData).map { _ =>
                //     Redirect(controllers.todo.routes.TodoController.trushPage())
                // }
            }
        )
    }

    def archive() = trush(formData => {
        TodoRepository().archiveAll(formData.ids.map(Todo.Id(_)))
    })

    def unarchive() = trush(formData => {
        TodoRepository().unarchiveAll(formData.ids.map(Todo.Id(_)))
    })

    def delete() = trush(formData => {
        TodoRepository().removeAll(formData.ids.map(Todo.Id(_)))
    })

    def update() = Action.async { implicit request =>
        SelectIdFormData.selectIdForm.bindFromRequest().fold (
            (formWithErrors: Form[SelectIdFormData]) => Future.successful(Redirect(controllers.todo.routes.TodoController.listPage())),
            (formData: SelectIdFormData) => {
                for {
                    _ <- TodoRepository().toggleStateAll(formData.ids.map(Todo.Id(_)))
                } yield {
                    Redirect(controllers.todo.routes.TodoController.listPage())
                }
            }
        )
    }
}
