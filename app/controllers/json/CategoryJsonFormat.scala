package controllers.json

import play.api.libs.json._
import java.time.LocalDateTime
import lib.model.Category

case class CategoryJson (
  id: Long,
  name: String,
  slug: String,
  color: Int,
  updatedAt: LocalDateTime,
  createdAt: LocalDateTime
)

object CategoryJson {
  implicit val format = Json.format[CategoryJson]

  def createCategoryJson(category: Category.EmbeddedId): CategoryJson = {
    CategoryJson(
      category.id,
      category.v.name,
      category.v.slug,
      category.v.color,
      category.v.updatedAt,
      category.v.createdAt
    )
  }
}