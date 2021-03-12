package lib.model

import ixias.model._
import java.time.LocalDateTime

import Category._

      //    `id`         BIGINT(20)   unsigned     NOT NULL AUTO_INCREMENT,
      //    `name`       VARCHAR(255)              NOT NULL,
      //    `slug`       VARCHAR(64) CHARSET ascii NOT NULL,
      //    `color`      TINYINT UNSIGNED          NOT NULL,
      //    `updated_at` timestamp                 NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      //    `created_at` timestamp                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
      //    PRIMARY KEY (`id`)
      //  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


case class Category(
    id: Option[Id],
    name: String,
    slug: String,
    color: Int,
    updatedAt: LocalDateTime = NOW,
    createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

object Category {
  val Id = the[Identity[Id]]
  type Id = Long @@ Category
  type WithNoId = Entity.WithNoId[Id, Category]
  type EmbeddedId = Entity.EmbeddedId[Id, Category]

  def apply(name: String, slug: String, color: Int): WithNoId = {
    new Entity.WithNoId(new Category(
      None, name, slug, color
    ))
  }
}