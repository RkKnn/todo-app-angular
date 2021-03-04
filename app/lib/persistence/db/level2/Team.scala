package lib.persistence.db.level2

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile
import ixias.persistence.model.Table

import lib.model.Team

case class TeamTable[P <: JdbcProfile]()(implicit val driver: P)
  extends Table[User, P] {
  import api._

  lazy val dsn = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/user"),
    "slave"  -> DataSourceName("ixias.db.mysql://slave/user")
  )

  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  class Table(tag: Tag) extends BasicTable(tag, "user") {
    import Team._
    // Columns
    /* @1 */ def id           = column[Team.Id]               ("id",           O.UInt64, O.PrimaryKey, O.AutoInc)
    /* @2 */ def employeeIds  = column[Seq[Employee.Id]] ("employee_ids", O.UInt64)
    /* @3 */ def name         = column[String]           ("name",         O.Utf8Char255)
    /* @4 */ def introduction = column[Option[String]]   ("introduction", O.Utf8Char255)
    /* @5 */ def updatedAt    = column[LocalDateTime]    ("updated_at",   O.TsCurrent)
    /* @6 */ def createdAt    = column[LocalDateTime]    ("created_at",   O.Ts)

    type TableElementTuple = (
      Option[Team.Id], Seq[Employee.Id], String, Option[String], LocalDateTime, LocalDateTime
    )

    // DB <=> Scala の相互のmapping定義
    def * = (id.?, employeeIds, name, introduction, updatedAt, createdAt) <> (
      // Tuple(table) => Model
      (t: TableElementTuple) => Team(
        t._1, t._2, t._3, t._4, t._5, t._6
      ),
      // Model => Tuple(table)
      (v: TableElementType) => Team.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, LocalDateTime.now(), t._6
      )}
    )
  }
}
