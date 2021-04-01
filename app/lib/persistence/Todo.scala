package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import lib.model.Todo
import slick.jdbc.JdbcProfile
import ixias.model.{Entity, IdStatus}
import shapeless.tag
import java.time.LocalDateTime
import lib.model.Category

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
        slick
        .filter(_.id.inSetBind(ids))
        .map(_.state)
        .update(Todo.StateType.DONE)
      }

    def unarchiveAll(ids: Seq[Id]): Future[Int] =
      RunDBAction(TodoTable) { slick =>
        slick
        .filter(_.id.inSetBind(ids))
        .map(_.state)
        .update(Todo.StateType.ACTIVE)
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

          activeIds = grouped.getOrElse(Todo.StateType.ACTIVE, Seq.empty)
          _ <- slick
            .filter(_.id.inSetBind(activeIds))
            .map(_.state)
            .update(Todo.StateType.IN_PROGRESS)

          inProgressIds = grouped.getOrElse(Todo.StateType.IN_PROGRESS, Seq.empty)
          _ <- slick
            .filter(_.id.inSetBind(inProgressIds))
            .map(_.state)
            .update(Todo.StateType.ACTIVE)
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

    def createCategoryRef(categoriyIdsFromTodo: Seq[Category.Id]): Future[TodoRepository.CategoryRef] = {
      val getCategories: Future[Seq[Category.EmbeddedId]] = RunDBAction(CategoryTable, "slave") {
        _.filter(_.id.inSetBind(categoriyIdsFromTodo)).result
      }

      for {
        categories <- getCategories

        categoryIdMap: Map[Category.Id, Category.EmbeddedId] = categories.map { category =>
          category.id -> category
        }.toMap

        categoryRef = categoriyIdsFromTodo.map { categoryId =>
          categoryId -> categoryIdMap.get(categoryId)
        }.toMap.collect {
          case (key, Some(value)) => key -> value
        }
      } yield categoryRef
    }

}

object TodoRepository {
  type CategoryRef = Map[Category.Id, Category.EmbeddedId]
}