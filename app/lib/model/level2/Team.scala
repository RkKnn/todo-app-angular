package lib.model.level2

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// 従業員を表すモデル
//~~~~~~~~~~~~~~~~~~~~
import Team._
case class Team(
  id:           Option[Id],
  employeeIds:  Seq[Employee.Id],
  name:         String,
  introduction: Option[String],
  updatedAt:    LocalDateTime = NOW,
  createdAt:    LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object Team {

  val  Id = the[Identity[Id]]
  type Id = Long @@ User
  type WithNoId = Entity.WithNoId [Id, User]
  type EmbeddedId = Entity.EmbeddedId[Id, User]
}
