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

case class SelectIdFormData(ids: Seq[Long])
object SelectIdFormData {
  val selectIdForm = Form(
    mapping(
      "ids" -> seq(longNumber)
    )(SelectIdFormData.apply)(SelectIdFormData.unapply)
  )
}