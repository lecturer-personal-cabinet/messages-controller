package com.lpc.services.messages

import cats._
import cats.implicits._
import com.lpc.db.DatabaseManager
import com.lpc.db.dao.DialogMessageDao
import com.lpc.services.dialog.DialogService
import javax.inject.Inject

trait DialogMessageService[F[_], DB[_]] {
  def insertDialogMessage(dialogMessage: DialogMessage): F[DialogMessage]
}

class DialogMessageServiceImpl[F[_] : Monad, DB[_]] @Inject()(dialogService: DialogService[F, DB],
                                                              dialogMessageDao: DialogMessageDao[DB],
                                                              dbManager: DatabaseManager[F, DB])
  extends DialogMessageService[F, DB]
    with DialogMessageMapper {

  override def insertDialogMessage(dialogMessage: DialogMessage): F[DialogMessage] = {
    val entityToSave = toEntity(dialogMessage)
    val result = dbManager.execute(dialogMessageDao.insert(entityToSave))
    result.map(toDto)
  }
}
