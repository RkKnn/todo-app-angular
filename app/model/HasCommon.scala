package model

/**
 * ViewValueCommonに関するデコレーター
 */
trait HasCommon extends ViewValueCommon {
  val common: ViewValueCommon
  val cssSrc: Seq[String] = common.cssSrc
  val jsSrc: Seq[String] = common.jsSrc
  val title: String = common.title
}