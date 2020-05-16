package com.lpc.actors

import java.net.InetSocketAddress

import akka.actor.{ActorRef, Props}
import com.lpc.actors.meta.RequestMapper
import com.lpc.actors.meta.actors.{MessageEvent, MessageReceivedEvent, SocketRequest}
import com.lpc.services.messages.MessagesService
import com.lpc.services.models.DialogMessage
import play.api.libs.json.Json
import redis.RedisClient
import redis.actors.RedisSubscriberActor
import redis.api.pubsub.{Message, PMessage}

import scala.concurrent.{ExecutionContext, Future}

object SubscribeActor {
  def props(redis: RedisClient,
            out: ActorRef,
            messagesService: MessagesService,
            userId: String)
           (implicit ec: ExecutionContext): Props = {
    Props(new SubscribeActor(redis, out, messagesService, channels = Seq(userId)))
  }
}

class SubscribeActor(redis: RedisClient,
                     out: ActorRef,
                     messagesService: MessagesService,
                     channels: Seq[String] = Nil,
                     patterns: Seq[String] = Nil) extends RedisSubscriberActor(
  new InetSocketAddress(redis.host, redis.port), channels, patterns,
  onConnectStatus = connected => {println(s"connected: $connected")}) {

  import cats.instances.future._
  import scala.concurrent.ExecutionContext.Implicits.global

  def onMessage(message: Message) {
    println(message.data.decodeString("UTF-8"))
    Json.parse(message.data.decodeString("UTF-8")).validateOpt[SocketRequest].asOpt.flatten match {
      case Some(value) =>
        RequestMapper.getEvent(value) map {
          case event: MessageEvent => handleMessageEvent(event).value
          case _ =>
            println("No event found")
            Future.successful(Unit)
        }
      case None =>
        println("Can't parse")
        Future.successful(Unit)
    }
  }

  def onPMessage(pmessage: PMessage): Unit = {}

  private def handleMessageEvent(event: MessageEvent) = {
    messagesService
      .getMessage(event.messageId)
      .map { result =>
        out ! MessageReceivedEvent(
          userId = event.senderId,
          message = DialogMessage(
            id = result.id,
            dialogId = result.dialogId,
            createdTs = result.createdTs,
            content = result.content,
            senderId = result.senderId,
            isRead = result.isRead
          )
        )
      }
  }
}
