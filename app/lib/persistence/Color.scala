package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import lib.model.{Color, Category}
import slick.jdbc.JdbcProfile
import ixias.model.{Entity, IdStatus}
import ixias.aws.qldb.dbio
import lib.persistence.db.ColorTable

case class ColorRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[Color.Id, Color, P]
  with db.SlickResourceProvider[P] {
    import api._
    def get(id: Id): Future[Option[EntityEmbeddedId]] =
      RunDBAction(ColorTable, "slave") { _
        .filter(_.id === id)
        .result.headOption
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
      RunDBAction(ColorTable) { slick =>
        slick returning slick.map(_.id) += entity.v
      }
    
    def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
        RunDBAction(ColorTable) { slick =>
          val row = slick.filter(_.id === entity.id)
          for {
            old <- row.result.headOption
            _ <- old match {
              case None => DBIO.successful(0)
              case Some(_) => row.update(entity.v)
            }
          } yield old
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

object ColorRepository {
  type ColorRef = Map[Category.EmbeddedId, Color.EmbeddedId]
}