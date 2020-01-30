package com.lpc.controller

import akka.actor.ActorSystem
import akka.stream.Materializer
import cats.effect.IO
import com.lpc.actors.DialogActor
import com.lpc.actors.chat.{ChatInEvent, ChatOutEvent, LookupBus}
import com.lpc.services.dialog.DialogService
import com.lpc.services.messages.DialogMessageService
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class ChatController @Inject()(cc: ControllerComponents,
                               lookupBus: LookupBus,
                               dialogService: DialogService[IO, DBIO],
                               dialogMessageService: DialogMessageService[IO, DBIO])
                              (implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {
  import com.lpc.actors.chat.ChatImplicits._

  def accept = WebSocket.accept[ChatInEvent, ChatOutEvent] { request =>
    ActorFlow.actorRef { out =>
      DialogActor.props(out, lookupBus, dialogService, dialogMessageService)(ExecutionContext.global)
    }
  }
}
