package com.lpc.database.repository

import com.lpc.database.meta.DialogEntity
import javax.inject.Inject
import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}

trait DialogRepository {
  def upsert(entity: DialogEntity): Future[DialogEntity]

  def findById(dialogId: String): Future[Option[DialogEntity]]
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
}
