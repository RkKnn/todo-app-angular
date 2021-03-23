package model.todo

import play.api.data._
import play.api.data.Forms._
import model.ViewValueHome
import model.HasCommon
import model.ViewValueCommon
import lib.model.{Category, Color}

// Topページのviewvalue
case class ViewValueCategoryList(
  common: ViewValueCommon,
  registerForm: Form[controllers.todo.CategoryRegisterFormData],
  selectIdForm: Form[controllers.todo.SelectIdFormData],
  colorRegisterForm: Form[controllers.todo.ColorRegisterFormData],
  colorSelectIdForm: Form[controllers.todo.SelectIdFormData],
  categoryList: Seq[Category.EmbeddedId],
  colorRef: lib.persistence.CategoryRepository.ColorRef,
  colorList: Seq[Color.EmbeddedId]
) extends HasCommon