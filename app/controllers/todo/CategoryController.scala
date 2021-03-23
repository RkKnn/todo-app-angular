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
import lib.model.{Category, Color}

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
      colorRef <- CategoryRepository().createColorRef(value)
      colorList <- ColorRepository().getAll
    } yield {
      val categoryListVV = ViewValueCategoryList(
        vv,
        CategoryRegisterFormData.form, SelectIdFormData.selectIdForm, ColorRegisterFormData.form, SelectIdFormData.selectIdForm,
        value, colorRef, colorList)
      Ok(views.html.todo.CategoryList(categoryListVV))
    }
  }

  def colorRegister() = Action.async { implicit req =>
    ColorRegisterFormData.form.bindFromRequest().fold (
      (formWithErrors: Form[ColorRegisterFormData]) => {
        for {
          value <- CategoryRepository().getAll
          colorRef <- CategoryRepository().createColorRef(value)
          colorList <- ColorRepository().getAll
        } yield { 
          val categoryListVV = ViewValueCategoryList(
            vv,
            CategoryRegisterFormData.form, SelectIdFormData.selectIdForm, formWithErrors, SelectIdFormData.selectIdForm,
            value, colorRef, colorList)
          BadRequest(views.html.todo.CategoryList(categoryListVV))
        }
      },
      (formData: ColorRegisterFormData) => {
        val color = Color(formData.colorcode)
        for {
          _ <- ColorRepository().add(color)
        } yield {
          Redirect(controllers.todo.routes.CategoryController.listPage())
        }
      }
    )
  }

  def colorDelete() = Action.async { implicit req =>
    SelectIdFormData.selectIdForm.bindFromRequest().fold (
      (formWithErrors: Form[SelectIdFormData]) => Future.successful(Redirect(controllers.todo.routes.CategoryController.listPage())),
      (formData: SelectIdFormData) => {
        for {
          _ <- {
            ColorRepository().removeAll(formData.ids.map(Color.Id(_)))
          }
        } yield {
          Redirect(controllers.todo.routes.CategoryController.listPage())
        }
      }
    )
  }

  def register() = Action.async { implicit req =>
    CategoryRegisterFormData.form.bindFromRequest().fold (
      (formWithErrors: Form[CategoryRegisterFormData]) => {
        for {
          value <- CategoryRepository().getAll
          colorRef <- CategoryRepository().createColorRef(value)
          colorList <- ColorRepository().getAll
        } yield { 
          val categoryListVV = ViewValueCategoryList(
            vv,
            formWithErrors, SelectIdFormData.selectIdForm, ColorRegisterFormData.form, SelectIdFormData.selectIdForm,
            value, colorRef, colorList)
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
