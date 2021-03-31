package controllers.json

import play.api.libs.json._
import java.time.LocalDateTime
import lib.model.Todo

case class TodoJson (
    id: Long,
    categoryId: Long,
    title: String,
    body: String,
    state: String,
    updatedAt: LocalDateTime,
    createdAt: LocalDateTime
)

object TodoJson {
  implicit val format = Json.format[TodoJson]

  def createTodoJson(todo: Todo.EmbeddedId): TodoJson = {
    TodoJson(
      todo.id,
      todo.v.categoryId,
      todo.v.title,
      todo.v.body,
      todo.v.state.toString(),
      todo.v.updatedAt,
      todo.v.createdAt
    )
  }
}