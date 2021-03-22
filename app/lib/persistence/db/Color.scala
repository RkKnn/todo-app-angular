package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile
import ixias.persistence.model.Table

import lib.model.Color

case class ColorTable[P <: JdbcProfile]()(implicit val driver: P) extends Table[Color, P] {
  import api._
  lazy val dsn = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/to_do"),
    "slave" -> DataSourceName("ixias.db.mysql://slave/to_do")
  )

  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  class Table(tag: Tag) extends BasicTable(tag, "color") {
    import Color._


    def id = column[Id]("id", O.UInt64, O.PrimaryKey, O.AutoInc)
    def colorcode = column[Int]("colorcode", O.UInt32)
    def updatedAt = column[LocalDateTime]("updated_at", O.TsCurrent)
    def createdAt = column[LocalDateTime]("created_at", O.Ts)

    type TableElementTuple = (Option[Id], Int, LocalDateTime, LocalDateTime)
    def * = (id.?, colorcode, updatedAt, createdAt) <> (
      (t: TableElementTuple) => Color.apply(t._1, t._2, t._3, t._4),
      (v: TableElementType) => Color.unapply(v).map { t => (
        t._1, t._2, LocalDateTime.now(), t._4
      )}
    )
  }
}