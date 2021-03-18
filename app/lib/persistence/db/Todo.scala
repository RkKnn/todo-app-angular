package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile
import ixias.persistence.model.Table

import lib.model.Todo
import lib.model.Category

case class TodoTable[P <: JdbcProfile]()(implicit val driver: P) extends Table[Todo, P] {
  import api._
  lazy val dsn = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/to_do"),
    "slave" -> DataSourceName("ixias.db.mysql://slave/to_do")
  )

  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  class Table(tag: Tag) extends BasicTable(tag, "to_do") {
    import Todo._

    def id = column[Id]("id", O.UInt64, O.PrimaryKey, O.AutoInc)
    def categoryId = column[Category.Id]("category_id", O.UInt64)
    def title = column[String]("title", O.Utf8Char255)
    def body = column[String]("body", O.Text)
    def state = column[StateT]("state", O.Int8)
    def updatedAt = column[LocalDateTime]("updated_at", O.TsCurrent)
    def createdAt = column[LocalDateTime]("created_at", O.Ts)

    type TableElementTuple = (Option[Id], Category.Id, String, String, StateT, LocalDateTime, LocalDateTime)

    def * = (id.?, categoryId, title, body, state, updatedAt, createdAt) <> (
      (t: TableElementTuple) => Todo(t._1, t._2, t._3, t._4, t._5, t._6, t._7),
      (v: TableElementType) => Todo.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, t._5, t._6, t._7
      )}
    )
  }
}

sealed abstract class StateType(val state: Int, name: String) {
  override def toString(): String = name
}

object StateType {
  object Active extends StateType(0, "active")
  object InProgress extends StateType(1, "inProgress")
  object Done extends StateType(2, "done")

  def toStateType(state: Int): StateType = {
    state match {
      case 0 => Active
      case 1 => InProgress
      case 2 => Done
    }
  }
}
