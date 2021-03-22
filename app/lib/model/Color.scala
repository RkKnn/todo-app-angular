package lib.model

import ixias.model._
import java.time.LocalDateTime

import Color._

      //    `id`         BIGINT(20)   UNSIGNED     NOT NULL AUTO_INCREMENT,
      //    `colorcode`  MEDIUMINT UNSIGNED        NOT NULL,
      //    `updated_at` TIMESTAMP                 NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      //    `created_at` TIMESTAMP                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
      //    PRIMARY KEY (`id`)
      //  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

case class Color(
    id: Option[Id],
    colorcode: Int,
    updatedAt: LocalDateTime = NOW,
    createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

object Color {
  val Id = the[Identity[Id]]
  type Id = Long @@ Color
  type WithNoId = Entity.WithNoId[Id, Color]
  type EmbeddedId = Entity.EmbeddedId[Id, Color]

  def apply(colorcode: Int): WithNoId = {
    new Entity.WithNoId(new Color(
      None, colorcode
    ))
  }

  def convert(color: Color): String = s"#${color.colorcode.toHexString}"
}