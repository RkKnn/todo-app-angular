package lib.model

import ixias.model._
import java.time.LocalDateTime

import Todo._
import ixias.util.EnumStatus
case class Todo(
    id: Option[Id],
    categoryId: Category.Id,
    title: String,
    body: String,
    state: StateType,
    updatedAt: LocalDateTime = NOW,
    createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

object Todo {
  val Id = the[Identity[Id]]
  type Id = Long @@ Todo
  type WithNoId = Entity.WithNoId[Id, Todo]
  type EmbeddedId = Entity.EmbeddedId[Id, Todo]

  def apply(categoryId: Category.Id, title: String, body: String, state: StateType): WithNoId = {
    new Entity.WithNoId(new Todo(
      None, categoryId, title, body, state
    ))
  }

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class StateType(val code: Short, val state: Int) extends EnumStatus
  object StateType extends EnumStatus.Of[StateType] {
    case object ACTIVE      extends StateType(code = 0, state = 0)
    case object IN_PROGRESS extends StateType(code = 1, state = 1)
    case object DONE        extends StateType(code = 2, state = 2)
  }
}