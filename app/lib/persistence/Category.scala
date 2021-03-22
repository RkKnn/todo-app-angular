package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import lib.model.{Category, Color}
import slick.jdbc.JdbcProfile
import ixias.model.{Entity, IdStatus}

case class CategoryRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[Category.Id, Category, P]
  with db.SlickResourceProvider[P] {
    import api._
    def get(id: Id): Future[Option[EntityEmbeddedId]] =
      RunDBAction(CategoryTable, "slave") { _
        .filter(_.id === id)
        .result.headOption
      }

    def getAll: Future[Seq[EntityEmbeddedId]] =
      RunDBAction(CategoryTable, "slave") {
        _.result
      }
    
    def createColorRef(categories: Seq[Category.EmbeddedId]): Future[ColorRepository.ColorRef] = {

      val getColors: Future[Seq[Color.EmbeddedId]] = RunDBAction(ColorTable, "slave") {
        _.filter(_.id.inSetBind(categories.map(category => Color.Id(category.v.color)))).result
      }

      for {
        colors <- getColors

        colorIdMap: Map[Color.Id, Color.EmbeddedId] = colors.map { color =>
          color.id -> color
        }.toMap

        colorRef = categories.map { category =>
          category -> colorIdMap.get(Color.Id(category.v.color))
        }.toMap.collect {
          case (key, Some(value)) => key -> value
        }
      } yield colorRef
    }

    def add(entity: EntityWithNoId): Future[Id] =
      RunDBAction(CategoryTable) { slick =>
        slick returning slick.map(_.id) += entity.v
      }
    
    def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
        RunDBAction(CategoryTable) { slick =>
          val row = slick.filter(_.id === entity.id)
          for {
            old <- row.result.headOption
            _ <- old match {
              case None => DBIO.successful(0)
              case Some(_) => row.update(entity.v)
            }
          } yield old
        }

    def removeAll(ids: Seq[Id]): Future[_] = {
        val removeCategory = RunDBAction(CategoryTable)(_.filter(_.id.inSetBind(ids)).delete)
        val removeTodo = RunDBAction(TodoTable)(_.filter(_.categoryId.inSetBind(ids)).delete)

        Future.sequence(Seq(removeCategory, removeTodo))
    }
    
    def remove(id: Id): Future[Option[EntityEmbeddedId]] = ???
        // RunDBAction(CategoryTable) { slick =>
        //   val row = slick.filter(_.id === id)
        //   for {
        //     old <- row.result.headOption
        //     _ <- old match {
        //       case None => DBIO.successful(0)
        //       case Some(_) => row.delete
        //     }
        //   } yield old
        // }
}