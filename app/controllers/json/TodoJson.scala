package controllers.json

import play.api.libs.json._
import java.time.LocalDateTime
import lib.model.Todo

case class TodoWrites (
    id: Long,
    categoryId: Long,
    title: String,
    body: String,
    state: String,
    updatedAt: LocalDateTime,
    createdAt: LocalDateTime
)

object TodoWrites {
  implicit val writes = Json.writes[TodoWrites]

  def createTodoWrites(todo: Todo.EmbeddedId): TodoWrites = {
    TodoWrites(
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