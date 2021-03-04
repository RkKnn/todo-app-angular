package lib.model.level2

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// 従業員を表すモデル
//~~~~~~~~~~~~~~~~~~~~
import Employee._
case class Employee(
  id:           Option[Id],
  teamId:       Option[Team.Id],
  name:         String,
  contact:      Option[String],
  onlineStatus: OnlineStatus,
  updatedAt:    LocalDateTime = NOW,
  createdAt:    LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object Employee {

  val  Id = the[Identity[Id]]
  type Id = Long @@ User
  type WithNoId = Entity.WithNoId [Id, User]
  type EmbeddedId = Entity.EmbeddedId[Id, User]

  // オンラインステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class OnlineStatus(val code: Short, val name: String) extends EnumStatus
  object Status extends EnumStatus.Of[Status] {
    case object IS_INACTIVE extends OnlineStatus(code = 0,   name = "無効")
    case object IS_ACTIVE   extends OnlineStatus(code = 100, name = "有効")
  }
}
