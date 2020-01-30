package com.lpc.db.dao

import com.lpc.db.Models.{DialogEntity, DialogId}
import com.lpc.tables
import slick.dbio
import slick.dbio._

trait DialogDao[DB[_]] {
  def getById(id: DialogId): DB[Option[DialogEntity]]

  def insert(entity: DialogEntity): DB[DialogEntity]

  def findByParticipants(participants: List[String]): DB[Option[DialogEntity]]
}

class DialogDaoImpl extends DialogDao[DBIO] {

  import com.lpc.db.ExtendedPostgresDriver.api._

  override def getById(id: DialogId): DBIO[Option[DialogEntity]] =
    tables.DialogTable.filter(_.chatId === id).result.headOption

  override def insert(entity: DialogEntity): dbio.DBIO[DialogEntity] =
    insertQuery += entity

  override def findByParticipants(participants: List[String]): dbio.DBIO[Option[DialogEntity]] =
    tables.DialogTable.filter(_.participants === participants).result.headOption

  private[this] val insertQuery = tables.DialogTable returning tables.DialogTable
    .map(_.chatId) into ((item, id) => item.copy(dialogId = id))
}
