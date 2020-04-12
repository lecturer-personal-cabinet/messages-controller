package com.lpc.database.repository

import com.lpc.database.meta.DialogParticipantEntity
import javax.inject.Inject
import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}

trait DialogParticipantRepository {
  def upsert(entity: DialogParticipantEntity): Future[DialogParticipantEntity]

  def findById(id: String): Future[Option[DialogParticipantEntity]]
}

class DefaultDialogParticipantRepository @Inject() (db: JdbcBackend#Database)(implicit ec: ExecutionContext)
  extends DialogParticipantRepository {
  import com.lpc.database.meta.ExtendedPostgresDriver.api._
  import com.lpc.database.meta.Tables._

  override def upsert(entity: DialogParticipantEntity): Future[DialogParticipantEntity] = {
    val query = DialogParticipantTable
      .map(t => (t.dialogId, t.userId))
      .returning(DialogParticipantTable.map(_.id))
      .+=((entity.dialogId, entity.userId))

    db.run(query).map(id => entity.copy(id = id))
  }

  override def findById(id: String): Future[Option[DialogParticipantEntity]] = {
    val query = DialogParticipantTable.filter(_.id === id).result

    db.run(query).map(_.headOption)
  }
}
