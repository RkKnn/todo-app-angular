package controllers.todo

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.i18n._

import lib.persistence.onMySQL.driver
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import model.ViewValueHome
import lib.persistence.{CategoryRepository, ColorRepository}
import model.todo.ViewValueCategoryList
import lib.model.Category

@Singleton
class CategoryController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
  val vv = ViewValueHome(
    title  = "カテゴリー一覧",
    cssSrc = Seq("main.css"),
    jsSrc  = Seq("main.js")
  )

  def listPage() = Action.async { implicit req =>
    for {
      value <- CategoryRepository().getAll
      colorRef <- ColorRepository().createColorRef(value)
    } yield {
      val categoryListVV = ViewValueCategoryList(
        vv,
        CategoryRegisterFormData.form, SelectIdFormData.selectIdForm,
        value, colorRef)
      Ok(views.html.todo.CategoryList(categoryListVV))
    }
  }

  def register() = Action.async { implicit req =>
    // for {
    //   value <- CategoryRepository().getAll
    // } yield {
    //   Redirect(controllers.todo.routes.CategoryController.listPage())
    // }
    CategoryRegisterFormData.form.bindFromRequest().fold (
      (formWithErrors: Form[CategoryRegisterFormData]) => {
        for {
          value <- CategoryRepository().getAll
          colorRef <- ColorRepository().createColorRef(value)
        } yield { 
          val categoryListVV = ViewValueCategoryList(
            vv,
            formWithErrors, SelectIdFormData.selectIdForm,
            value, colorRef)
          BadRequest(views.html.todo.CategoryList(categoryListVV))
        }
      },
      (formData: CategoryRegisterFormData) => {
        val category = Category(formData.name, formData.slug, formData.color)
        for {
          _ <- CategoryRepository().add(category)
        } yield {
          Redirect(controllers.todo.routes.CategoryController.listPage())
        }
      }
    )
  }

  def delete() = Action.async { implicit req =>
    SelectIdFormData.selectIdForm.bindFromRequest().fold (
      (formWithErrors: Form[SelectIdFormData]) => Future.successful(Redirect(controllers.todo.routes.CategoryController.listPage())),
      (formData: SelectIdFormData) => {
        for {
          _ <- {
            CategoryRepository().removeAll(formData.ids.map(Category.Id(_)))
          }
        } yield {
          Redirect(controllers.todo.routes.CategoryController.listPage())
        }
      }
    )
  }
}
