package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import lib.model.Todo
import slick.jdbc.JdbcProfile
import ixias.model.{Entity, IdStatus}
import shapeless.tag
import lib.persistence.db.StateType
import java.time.LocalDateTime

case class TodoRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[Todo.Id, Todo, P]
  with db.SlickResourceProvider[P] {
    import api._

    def getAll: Future[Seq[EntityEmbeddedId]] =
      RunDBAction(TodoTable, "slave") { _
        // .sortBy(_.updatedAt.desc)
        .result
      }

    def get(id: Id): Future[Option[EntityEmbeddedId]] =
      RunDBAction(TodoTable, "slave") { _
        .filter(_.id === id)
        .result.headOption
      }

    def add(entity: EntityWithNoId): Future[Id] =
      RunDBAction(TodoTable) { slick =>
        slick returning slick.map(_.id) += entity.v
      }
      
    def archiveAll(ids: Seq[Id]): Future[Int] =
      RunDBAction(TodoTable) { slick: TodoTable.Query =>
        val stateColumns = for {
          row <- slick.filter(_.id.inSetBind(ids))
        } yield row.state
        stateColumns.update(StateType.Archive.state)

        // slick
        // .filter(_.id.inSetBind(ids))
        // .map(_.state)
        // .update(StateType.Archive.state)
      }

    def unarchiveAll(ids: Seq[Id]): Future[Int] =
      RunDBAction(TodoTable) { slick =>
        val stateColumns = for {
          row <- slick.filter(_.id.inSetBind(ids))
        } yield row.state
        stateColumns.update(StateType.Active.state)

        // slick
        // .filter(_.id.inSetBind(ids))
        // .map(_.state)
        // .update(StateType.Active.state)
      }
    
    def toggleStateAll(ids: Seq[Id]): Future[_] =
      RunDBAction(TodoTable) { slick: TodoTable.Query =>
        for {
          old <- slick
            .filter(_.id.inSetBind(ids))
            .map(todo => (todo.id, todo.state))
            .result

          grouped = old.groupBy(_._2)
            .transform((key, values) => values.map(value => Todo.Id(value._1)))

          activeIds = grouped.getOrElse(StateType.Active.state, Seq.empty)
          _ <- slick
            .filter(_.id.inSetBind(activeIds))
            .map(_.state)
            .update(StateType.Done.state)

          doneIds = grouped.getOrElse(StateType.Done.state, Seq.empty)
          _ <- slick
            .filter(_.id.inSetBind(doneIds))
            .map(_.state)
            .update(StateType.Active.state)
        } yield old
      }
    
    def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
        RunDBAction(TodoTable) { slick =>
          val row = slick.filter(_.id === entity.id)
          for {
            old <- row.result.headOption
            _ <- old match {
              case None => DBIO.successful(0)
              case Some(_) => row.update(entity.v)
            }
          } yield old
        }
      
    def removeAll(ids: Seq[Id]): Future[Int] =
        RunDBAction(TodoTable) { slick =>
          slick.filter(_.id.inSetBind(ids)).delete
        }
    
    def remove(id: Id): Future[Option[EntityEmbeddedId]] =
        RunDBAction(TodoTable) { slick =>
          val row = slick.filter(_.id === id)
          for {
            old <- row.result.headOption
            _ <- old match {
              case None => DBIO.successful(0)
              case Some(_) => row.delete
            }
          } yield old
        }
}