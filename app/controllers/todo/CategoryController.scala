package controllers.todo

import javax.inject._
import play.api.mvc._

import lib.persistence.onMySQL.driver
import scala.concurrent.ExecutionContext.Implicits.global
import model.ViewValueHome
import lib.persistence.CategoryRepository
import model.todo.ViewValueCategoryList

@Singleton
class CategoryController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  val vv = ViewValueHome(
    title  = "カテゴリー一覧",
    cssSrc = Seq("main.css"),
    jsSrc  = Seq("main.js")
  )

  def listPage() = Action.async { implicit req =>
    CategoryRepository().getAll.map { value =>
      Ok(views.html.todo.CategoryList(ViewValueCategoryList(vv, value.map(_.v))))
    }
  }
}
