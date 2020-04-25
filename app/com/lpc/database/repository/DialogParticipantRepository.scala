package com.lpc.database.repository

import com.lpc.database.meta.DialogParticipantEntity
import javax.inject.Inject
import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait DialogParticipantRepository {
  def upsert(entity: DialogParticipantEntity): Future[DialogParticipantEntity]

  def findById(id: String): Future[Option[DialogParticipantEntity]]

  def insertIfNotExists(entity: Seq[DialogParticipantEntity]): Future[Seq[DialogParticipantEntity]]
}

class DefaultDialogParticipantRepository @Inject() (db: JdbcBackend#Database)(implicit ec: ExecutionContext)
  extends DialogParticipantRepository {
  import com.lpc.database.meta.ExtendedPostgresDriver.api._
  import com.lpc.database.meta.Tables._

  override def upsert(entity: DialogParticipantEntity): Future[DialogParticipantEntity] = {
    db.run(insertQuery(entity))
  }

  override def findById(id: String): Future[Option[DialogParticipantEntity]] = {
    val query = DialogParticipantTable.filter(_.id === id).result

    db.run(query).map(_.headOption)
  }

  override def insertIfNotExists(entity: Seq[DialogParticipantEntity]): Future[Seq[DialogParticipantEntity]] = {
    println("[DB]: insertIfNotExists")
    val query = DBIO.sequence(entity.map(insertIfNotExistsQuery))
    db.run(query)
  }

  private def insertIfNotExistsQuery(entity: DialogParticipantEntity) = {
    val exists = DialogParticipantTable
      .filter(obj =>
        (obj.dialogId === entity.dialogId) &&
        (obj.userId === entity.userId))
      .exists
      .result

    exists.flatMap {
      case true => DBIO.successful(entity)
      case false => insertQuery(entity)
    }
  }

  private def insertQuery(entity: DialogParticipantEntity): DBIO[DialogParticipantEntity] = {
    val query = DialogParticipantTable
      .map(t => (t.dialogId, t.userId))
      .returning(DialogParticipantTable.map(_.id))
      .+=((entity.dialogId, entity.userId))

    query.map(result => entity.copy(id = result))
  }
}
