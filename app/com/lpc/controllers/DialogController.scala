package com.lpc.controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.lpc.actors.UserManagementActor
import com.lpc.actors.meta.UsersEventBus
import com.lpc.actors.meta.actors._
import com.lpc.services.messages.MessagesService
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import scala.concurrent.ExecutionContext

class DialogController @Inject()(cc: ControllerComponents, messagesService: MessagesService, lookupBus: UsersEventBus)
                                (implicit system: ActorSystem, mat: Materializer, ex: ExecutionContext)
  extends AbstractController(cc) {
  import com.lpc.actors.meta.ModelImplicits._

  def accept: WebSocket = WebSocket.accept[SocketRequest, MessageEventOut] { request =>
    ActorFlow.actorRef { out =>
      UserManagementActor.props(out, lookupBus, messagesService)
    }
  }
}
