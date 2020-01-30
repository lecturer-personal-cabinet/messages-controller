package com.lpc.wiring

import akka.actor.ActorSystem
import cats.effect.{ContextShift, IO}
import com.lpc.actors.chat.{LookupBus, LookupBusImpl}
import com.lpc.db.SlickDatabaseManager
import com.lpc.db.dao.{DialogDaoImpl, DialogMessageDaoImpl}
import com.lpc.services.dialog.DialogServiceImpl
import com.lpc.services.messages.DialogMessageServiceImpl
import play.api.db.slick.{DbName, SlickComponents}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

trait GlobalModule extends SlickComponents {
  import com.softwaremill.macwire._

  implicit val system: ActorSystem = ActorSystem()
  implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)

  lazy val dbConfig = slickApi.dbConfig[JdbcProfile](DbName("default"))
  lazy val DatabaseManager: SlickDatabaseManager[IO, DBIO] = wire[SlickDatabaseManager[IO, DBIO]]

  lazy val DialogDao: DialogDaoImpl = wire[DialogDaoImpl]
  lazy val DialogMessageDao: DialogMessageDaoImpl = wire[DialogMessageDaoImpl]

  lazy val LookupBus: LookupBus = wire[LookupBusImpl]
  lazy val DialogService: DialogServiceImpl[IO, DBIO] = wire[DialogServiceImpl[IO, DBIO]]
  lazy val DialogMessageService: DialogMessageServiceImpl[IO, DBIO] = wire[DialogMessageServiceImpl[IO, DBIO]]
}
