package controllers.json

import play.api.libs.json.Json

case class RegisterJson (
  categoryId: Long,
  title: String,
  body: String,
  state: String
)
object RegisterJson {
  implicit val reads = Json.reads[RegisterJson]
}