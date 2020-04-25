package com.lpc.database.repository

import com.lpc.database.meta.{DialogEntity, DialogParticipantEntity}
import javax.inject.Inject
import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}

trait DialogRepository {
  def upsert(entity: DialogEntity): Future[DialogEntity]

  def findById(dialogId: String): Future[Option[DialogEntity]]

  def insertIfNotExists(entity: DialogEntity): Future[DialogEntity]

  def findByParticipants(participants: Seq[String]): Future[Option[DialogEntity]]
}

class DefaultDialogRepository @Inject() (db: JdbcBackend#Database) (implicit ec: ExecutionContext) extends DialogRepository {
  import com.lpc.database.meta.ExtendedPostgresDriver.api._
  import com.lpc.database.meta.Tables._

  override def upsert(entity: DialogEntity): Future[DialogEntity] = {
    val query = DialogTable
      .map(t => (t.name))
      .returning(DialogTable.map(_.id))
      .+=((entity.name))

    db.run(query).map(id => entity.copy(id = id))
  }

  override def findById(dialogId: String): Future[Option[DialogEntity]] = {
    val query = DialogTable.filter(_.id === dialogId).result
    db.run(query).map(_.headOption)
  }

  override def insertIfNotExists(entity: DialogEntity): Future[DialogEntity] = {
    val exists = DialogParticipantTable.filter(_.id === entity.id).exists.result

    db.run(exists).flatMap {
      case true => Future.successful(entity)
      case false => upsert(entity)
    }
  }

  override def findByParticipants(participants: Seq[String]): Future[Option[DialogEntity]] = {
    val query = DialogParticipantTable.filter(_.userId inSet participants)
      .join(DialogTable).on(_.dialogId === _.id)
      .map { case (_, t) => t }
      .result
      .headOption

    query.statements.foreach(println)

    db.run(query)
  }
}
