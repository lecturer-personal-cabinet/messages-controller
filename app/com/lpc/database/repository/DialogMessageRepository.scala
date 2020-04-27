package com.lpc.database.repository

import com.lpc.database.meta.DialogMessageEntity
import javax.inject.Inject
import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}

trait DialogMessageRepository {
  def upsert(entity: DialogMessageEntity): Future[DialogMessageEntity]

  def findById(dialogMessageId: String): Future[Option[DialogMessageEntity]]

  def getUnreadMessagesCount(userId: String): Future[Int]
}

class DefaultDialogMessageRepository @Inject()(db: JdbcBackend#Database)(implicit ec: ExecutionContext)
  extends DialogMessageRepository {

  import com.lpc.database.meta.ExtendedPostgresDriver.api._
  import com.lpc.database.meta.Tables._

  override def upsert(entity: DialogMessageEntity): Future[DialogMessageEntity] = {
    val query = DialogMessageTable
      .map(t => (t.content, t.dialogId, t.senderId, t.createdTs))
      .returning(DialogMessageTable.map(_.id))
      .+=((entity.content, entity.dialogId, entity.senderId, entity.createdTs))

    db.run(query).map(id => entity.copy(id = id))
  }

  override def findById(dialogMessageId: String): Future[Option[DialogMessageEntity]] = {
    val query = DialogMessageTable.filter(_.id === dialogMessageId).result

    db.run(query).map(_.headOption)
  }

  override def getUnreadMessagesCount(userId: String): Future[Int] = {
    val query = DialogParticipantTable.filter(_.userId === userId)
      .join(DialogMessageTable).on(_.dialogId === _.dialogId)
      .filter { case (_, message) => (message.senderId =!= userId) && !message.isRead }
      .map { case (_, message) => message }
      .result

    db.run(query).map(_.length)
  }
}
