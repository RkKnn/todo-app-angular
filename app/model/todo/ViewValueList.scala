package model.todo

import play.api.data._
import play.api.data.Forms._
import model.ViewValueHome
import model.HasCommon
import model.ViewValueCommon

// Topページのviewvalue
case class ViewValueList(
  common: ViewValueCommon,
  registerForm: Form[controllers.todo.RegisterFormData],
  deleteForm: Form[controllers.todo.DeleteFormData],
  todoList: Seq[lib.model.Todo]
) extends HasCommon


