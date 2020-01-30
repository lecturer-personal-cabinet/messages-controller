package com.lpc.db.dao

import com.lpc.db.Models.DialogMessageEntity
import com.lpc.tables._
import slick.dbio._

trait DialogMessageDao[DB[_]] {
  def insert(entity: DialogMessageEntity): DB[DialogMessageEntity]
}

class DialogMessageDaoImpl extends DialogMessageDao[DBIO] {

  import com.lpc.db.ExtendedPostgresDriver.api._

  override def insert(entity: DialogMessageEntity): DBIO[DialogMessageEntity] =
    DialogMessageTable
      .map(m => (m.dialogId, m.sender, m.content))
      .returning(DialogMessageTable)
      .+=((entity.dialogId, entity.sender, entity.content))
}