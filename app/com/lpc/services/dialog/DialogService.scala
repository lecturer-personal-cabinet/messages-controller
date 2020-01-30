package com.lpc.services.dialog

import cats.Monad
import cats.implicits._
import com.lpc.db.DatabaseManager
import com.lpc.db.Models.{DialogEntity, DialogId}
import com.lpc.db.dao.DialogDao
import javax.inject.Inject

trait DialogService[F[_], DB[_]] {
  def getOrCreate(participants: List[String]): F[Dialog]

  def getByDialogId(dialogId: DialogId): F[Option[Dialog]]
}

class DialogServiceImpl[F[_] : Monad, DB[_]] @Inject()(dialogDao: DialogDao[DB],
                                                       dbManager: DatabaseManager[F, DB])
  extends DialogService[F, DB]
    with DialogMapper {

  override def getOrCreate(participants: List[String]): F[Dialog] = {
    for {
      maybeExistingDialog <- dbManager.execute(dialogDao.findByParticipants(participants))
      existingDialog <- maybeExistingDialog match {
        case Some(existingDialog) => existingDialog.pure[F]
        case None => dbManager.execute(dialogDao.insert(DialogEntity(None, participants)))
      }
      resultDto = toDto(existingDialog)
    } yield resultDto
  }

  override def getByDialogId(dialogId: DialogId): F[Option[Dialog]] =
    for {
      maybeExistingDialog <- dbManager.execute(dialogDao.getById(dialogId))
      resultDto = maybeExistingDialog.map(toDto)
    } yield resultDto
}
