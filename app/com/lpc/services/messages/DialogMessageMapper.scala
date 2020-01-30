package com.lpc.services.messages

import com.lpc.db.Models.{DialogId, DialogMessageEntity, DialogMessageId}

case class DialogMessage(dialogMessageId: Option[DialogMessageId], dialogId: DialogId, sender: String, content: String)

trait DialogMessageMapper {
  def toDto(entity: DialogMessageEntity) = DialogMessage(entity.messageId,
    entity.dialogId,
    entity.sender,
    entity.content)

  def toEntity(dto: DialogMessage) = DialogMessageEntity(dto.dialogMessageId,
    dto.dialogId,
    dto.sender,
    dto.content)
}
