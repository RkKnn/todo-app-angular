package model.todo

import play.api.data._
import play.api.data.Forms._
import model.ViewValueHome
import model.HasCommon
import model.ViewValueCommon

// Topページのviewvalue
case class ViewValueCategoryList(
  common: ViewValueCommon,
  registerForm: Form[controllers.todo.CategoryRegisterFormData],
  categoryList: Seq[lib.model.Category]
) extends HasCommon


