package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile
import ixias.persistence.model.Table

import lib.model.Todo

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
    def categoryId = column[Long]("category_id", O.UInt64)
    def title = column[String]("title", O.Utf8Char255)
    def body = column[String]("body", O.Text)
    def state = column[Int]("state", O.Int8)

    type TableElementTuple = (Option[Id], Long, String, String, Int)

    def * = (id.?, categoryId, title, body, state) <> (
      (t: TableElementTuple) => Todo(t._1, t._2, t._3, t._4, t._5),
      (v: TableElementType) => Todo.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, t._5
      )}
    )
  }
}