package controllers.todo
import play.api.data.Forms._
import play.api.data._

case class RegisterFormData(title: String, body: String)
object RegisterFormData {
  val registerForm = Form(
    mapping(
      "title" -> nonEmptyText(maxLength = 255),
      "body" -> nonEmptyText()
    )(RegisterFormData.apply)(RegisterFormData.unapply)
  )
}

case class DeleteFormData(ids: Seq[Long])
object DeleteFormData {
  val deleteForm = Form(
    mapping(
      "ids" -> seq(longNumber)
    )(DeleteFormData.apply)(DeleteFormData.unapply)
  )
}