package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile
import ixias.persistence.model.Table

import lib.model.Category

case class CategoryTable[P <: JdbcProfile]()(implicit val driver: P) extends Table[Category, P] {
  import api._
  lazy val dsn = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/to_do"),
    "slave" -> DataSourceName("ixias.db.mysql://slave/to_do")
  )

  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  class Table(tag: Tag) extends BasicTable(tag, "to_do_category") {
    import Category._

    def id = column[Id]("id", O.UInt64, O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.Utf8Char255)
    def slug = column[String]("slug", O.AsciiChar64)
    def color = column[Int]("color", O.UInt8)
    def updatedAt = column[LocalDateTime]("updated_at", O.TsCurrent)
    def createdAt = column[LocalDateTime]("created_at", O.Ts)

    type TableElementTuple = (Option[Id], String, String, Int, LocalDateTime, LocalDateTime)

    def * = (id.?, name, slug, color, updatedAt, createdAt) <> (
      (t: TableElementTuple) => Category(t._1, t._2, t._3, t._4, t._5, t._6),
      (v: TableElementType) => Category.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, t._5, t._6
      )}
    )
  }
}

// TODO: 非常に良くないのでテーブルを作るかする
object Color {
  def convert(color: Int): String = {
    color match {
      // db number => css color
      case 0 => "#888"
      case 1 => "#ff0000"
      case 2 => "#00ff00"
      case 3 => "#0000ff"
      case _ => "#aaa"
    }
  }
}