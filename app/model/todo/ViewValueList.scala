package model.todo

import play.api.data._
import play.api.data.Forms._
import model.ViewValueHome
import model.HasCommon
import model.ViewValueCommon
import lib.model.Todo
import lib.persistence.TodoRepository

// Topページのviewvalue
case class ViewValueList(
  common: ViewValueCommon,
  registerForm: Form[controllers.todo.RegisterFormData],
  selectIdForm: Form[controllers.todo.SelectIdFormData],
  todoList: Seq[Todo.EmbeddedId],
  category: TodoRepository.CategoryRef,
  colorRef: lib.persistence.CategoryRepository.ColorRef
) extends HasCommon


