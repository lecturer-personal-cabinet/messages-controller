package com.lpc.controllers

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import com.lpc.actors.{SubscribeActor, UserManagementActor}
import com.lpc.actors.meta.UsersEventBus
import com.lpc.actors.meta.actors._
import com.lpc.services.messages.MessagesService
import com.typesafe.config.ConfigFactory
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import redis.RedisClient

import scala.concurrent.ExecutionContext

class DialogController @Inject()(cc: ControllerComponents, messagesService: MessagesService, lookupBus: UsersEventBus)
                                (implicit system: ActorSystem, mat: Materializer, ex: ExecutionContext)
  extends AbstractController(cc) {
  import com.lpc.actors.meta.ModelImplicits._

  private lazy val RedisConfig = ConfigFactory.load().getConfig("redis")
  private lazy val Redis: RedisClient = RedisClient(RedisConfig.getString("host"), RedisConfig.getInt("port"))

  def accept(userId: String): WebSocket = WebSocket.accept[SocketRequest, MessageEventOut] { _ =>
    ActorFlow.actorRef { out =>
      SubscribeActor.props(Redis, out, messagesService, userId)
    }
  }
}
