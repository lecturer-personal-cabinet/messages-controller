package com.lpc.services.dialog

import cats.implicits._
import com.lpc.db.Models.{DialogEntity, DialogId}

case class Dialog(id: Option[String], participants: List[String])

trait DialogMapper {
  def toDto(entity: DialogEntity): Dialog = Dialog(entity.dialogId.map(_.value), entity.participants)
  def toEntity(dto: Dialog): DialogEntity = DialogEntity(dto.id.map(DialogId), dto.participants)
}
