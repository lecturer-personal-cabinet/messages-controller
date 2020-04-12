package com.lpc.services.messages

import com.lpc.database.meta.DialogMessageEntity
import com.lpc.database.repository.DialogMessageRepository
import com.lpc.services.models.DialogMessage
import com.lpc.services.services.AsyncServiceResult
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

trait MessagesService {
  def insertOrUpdate(message: DialogMessage): AsyncServiceResult[DialogMessage]
}

class DefaultMessagesService @Inject() (repository: DialogMessageRepository)
                                       (implicit ec: ExecutionContext)
  extends MessagesService {
  import cats.implicits._

  override def insertOrUpdate(message: DialogMessage): AsyncServiceResult[DialogMessage] = {
    val entity: DialogMessageEntity = MessagesMapper.toEntity(message)
    val result: Future[DialogMessageEntity] = repository.upsert(entity)
    AsyncServiceResult.liftF(result).map(MessagesMapper.toDto)
  }
}
