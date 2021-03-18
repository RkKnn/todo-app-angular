package controllers.todo
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints

case class RegisterFormData(title: String, body: String, category: Int)
object RegisterFormData {
  val registerForm = Form(
    mapping(
      "title" -> nonEmptyText(maxLength = 255),
      "body" -> nonEmptyText(),
      "category" -> number()
    )(RegisterFormData.apply)(RegisterFormData.unapply)
  )
}

case class CategoryRegisterFormData(name: String, slug: String, color: Int)
object CategoryRegisterFormData {
  val alphabetOrNumber = Constraints.pattern("[\\w\\d]+".r)
  val form = Form(
    mapping(
      "name" -> nonEmptyText(maxLength = 255),
      "slug" -> nonEmptyText(maxLength = 64).verifying(alphabetOrNumber),
      "color" -> number(min = 0, max = 255)
    )(CategoryRegisterFormData.apply)(CategoryRegisterFormData.unapply)
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