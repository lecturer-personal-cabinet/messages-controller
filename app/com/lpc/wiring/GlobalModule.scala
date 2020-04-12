package com.lpc.wiring

import akka.actor.ActorSystem
import com.lpc.actors.meta.UsersEventBus
import com.lpc.database.repository.{DefaultDialogMessageRepository, DefaultDialogParticipantRepository, DefaultDialogRepository, DialogMessageRepository, DialogParticipantRepository, DialogRepository}
import com.lpc.services.messages.{DefaultMessagesService, MessagesService}
import play.api.db.slick.{DbName, SlickComponents}
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.ExecutionContext

trait GlobalModule extends SlickComponents {
  import com.softwaremill.macwire._

  implicit val system: ActorSystem = ActorSystem("actor-system")
  implicit val ex: ExecutionContext = ExecutionContext.global

  lazy val Database: JdbcBackend#Database = slickApi.dbConfig[JdbcProfile](DbName("default")).db

  lazy val DialogMessageRepository: DialogMessageRepository = wire[DefaultDialogMessageRepository]
  lazy val DialogParticipantRepository: DialogParticipantRepository = wire[DefaultDialogParticipantRepository]
  lazy val DialogRepository: DialogRepository = wire[DefaultDialogRepository]

  lazy val MessagesService: MessagesService = wire[DefaultMessagesService]

  lazy val DialogLookupBus: UsersEventBus = wire[UsersEventBus]
}
