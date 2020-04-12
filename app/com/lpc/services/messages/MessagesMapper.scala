package com.lpc.services.messages

import java.sql.Timestamp

import com.lpc.database.meta.DialogMessageEntity
import com.lpc.services.models.DialogMessage

object MessagesMapper {
  def toDto(entity: DialogMessageEntity): DialogMessage =
    DialogMessage (
      id = entity.id,
      dialogId = entity.dialogId,
      createdTs = entity.createdTs.map(_.toLocalDateTime),
      content = entity.content)

  def toEntity(dto: DialogMessage): DialogMessageEntity =
    DialogMessageEntity (
      id = dto.id,
      dialogId = dto.dialogId,
      createdTs = dto.createdTs.map(Timestamp.valueOf),
      content = dto.content)
}
