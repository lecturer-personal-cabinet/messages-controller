package com.lpc.services.messages

import java.sql.Timestamp

import com.lpc.database.meta.{DialogEntity, DialogMessageEntity, DialogParticipantEntity}
import com.lpc.services.models.{Dialog, DialogMessage, DialogParticipant}

object MessagesMapper {
  def toDto(entity: DialogMessageEntity): DialogMessage =
    DialogMessage (
      senderId = entity.senderId,
      id = entity.id,
      dialogId = entity.dialogId,
      createdTs = entity.createdTs.map(_.toLocalDateTime),
      content = entity.content)

  def toEntity(dto: DialogMessage): DialogMessageEntity =
    DialogMessageEntity (
      senderId = dto.senderId,
      id = dto.id,
      dialogId = dto.dialogId,
      createdTs = dto.createdTs.map(Timestamp.valueOf),
      content = dto.content)

  def toDto(entity: DialogEntity): Dialog =
    Dialog (
      id = entity.id,
      name = entity.name)

  def toEntity(dto: Dialog): DialogEntity =
    DialogEntity(
      id = dto.id,
      name = dto.name)

  def toDto(entity: DialogParticipantEntity): DialogParticipant =
    DialogParticipant(
      id = entity.id,
      userId = entity.userId,
      dialogId = entity.dialogId)

  def toEntity(dto: DialogParticipant): DialogParticipantEntity =
    DialogParticipantEntity(
      id = dto.id,
      userId = dto.userId,
      dialogId = dto.dialogId)
}
