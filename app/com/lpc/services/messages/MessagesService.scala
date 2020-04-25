package com.lpc.services.messages

import com.lpc.database.meta.{DialogEntity, DialogMessageEntity, DialogParticipantEntity}
import com.lpc.database.repository.{DialogMessageRepository, DialogParticipantRepository, DialogRepository}
import com.lpc.services.models.{Dialog, DialogMessage, DialogParticipant}
import com.lpc.services.services.{AsyncServiceResult, NotFoundError}
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

trait MessagesService {
  def insertOrUpdateMessage(message: DialogMessage): AsyncServiceResult[DialogMessage]
  def insertIfNotExistsDialog(dialog: Dialog): AsyncServiceResult[Dialog]
  def findDialog(participants: Seq[String]): AsyncServiceResult[Dialog]
  def insertIfNotExistsDialogParticipants(dialogParticipant: Seq[DialogParticipant]): AsyncServiceResult[Seq[DialogParticipant]]
}

class DefaultMessagesService @Inject() (dialogMessageRepository: DialogMessageRepository,
                                        dialogParticipantRepository: DialogParticipantRepository,
                                        dialogRepository: DialogRepository)
                                       (implicit ec: ExecutionContext)
  extends MessagesService {
  import cats.implicits._
  import AsyncServiceResult._

  override def insertOrUpdateMessage(message: DialogMessage): AsyncServiceResult[DialogMessage] = {
    println("Insert or update message")
    val entity: DialogMessageEntity = MessagesMapper.toEntity(message)
    val result: Future[DialogMessageEntity] = dialogMessageRepository.upsert(entity)

    AsyncServiceResult.liftF(result).map(MessagesMapper.toDto)
      .logInfo(e => s"[insertOrUpdateMessage] Info: ${e}")
      .logFailure(err => s"[insertOrUpdateMessage] Error: ${err}")
  }

  override def insertIfNotExistsDialog(dialog: Dialog): AsyncServiceResult[Dialog] = {
    println("Insert if not exists dialog")
    val entity: DialogEntity = MessagesMapper.toEntity(dialog)
    val result = dialogRepository.insertIfNotExists(entity)
    AsyncServiceResult.liftF(result).map(MessagesMapper.toDto)
      .logInfo(e => s"[insertIfNotExistsDialog] Info: ${e}")
      .logFailure(err => s"[insertIfNotExistsDialog] Error: ${err}")
  }

  override def insertIfNotExistsDialogParticipants(dialogParticipants: Seq[DialogParticipant]): AsyncServiceResult[Seq[DialogParticipant]] = {
    println("Insert if not exists dialog participants")
    val entity = dialogParticipants.map(MessagesMapper.toEntity)
    val result = dialogParticipantRepository.insertIfNotExists(entity)
    AsyncServiceResult.liftF(result).map(_.map(MessagesMapper.toDto))
      .logInfo(e => s"[insertIfNotExistsDialogParticipants] Info: ${e}")
      .logFailure(err => s"[insertIfNotExistsDialogParticipants] Error: ${err}")
  }

  override def findDialog(participants: Seq[String]): AsyncServiceResult[Dialog] = {
    println("Find dialog")
    dialogRepository.findByParticipants(participants)
      .orNotFound(NotFoundError())
      .map(MessagesMapper.toDto)
      .logInfo(e => s"[findDialog] Info: ${e}")
      .logFailure(err => s"[findDialog] Error: ${err}")
  }
}
