package lib.model

import ixias.model._
import java.time.LocalDateTime

import Todo._
case class Todo(
    id: Option[Id],
    category_id: Long,
    title: String,
    body: String,
    state: Int,
    updatedAt: LocalDateTime = NOW,
    createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

object Todo {
  val Id = the[Identity[Id]]
  type Id = Long @@ Todo
  type WithNoId = Entity.WithNoId[Id, Todo]
  type EmbeddedId = Entity.EmbeddedId[Id, Todo]

  // def apply(title: String): WithNoId = {
  //   new Entity.WithNoId(new Todo(
  //     None, title
  //   ))
  // }
  def apply(title: String): Todo = {
    new Todo(None, 1, "aiueo", "body", 0)
  }
}