package com.lpc.actors.meta

import com.lpc.services.models.DialogMessage
import play.api.libs.json.{Format, JsValue, Json}

package object actors {
  sealed trait MessageEventIn

  sealed trait MessageEventOut {
    def eventType: String
    def userId: String
  }

  case class SocketRequest(
    data: JsValue,
    eventType: String)
  object SocketRequest {
    implicit val fmt: Format[SocketRequest] = Json.format
  }

  case class MessageEvent(
     receiverId: String,
     senderId: String,
     messageId: String,
     dialogId: String) extends MessageEventIn
  object MessageEvent {
    implicit val fmt: Format[MessageEvent] = Json.format
  }

  case class NotificationEvent(
    userId: String,
    content: String,
    eventType: String = "notification-event") extends MessageEventOut

  case class MessageSentEvent(
     userId: String,
     eventType: String = "message-sent") extends MessageEventOut

  case class MessageReceivedEvent(
     userId: String,
     message: DialogMessage,
     eventType: String = "message-received") extends MessageEventOut

  case class MetricsEvent(
     userId: String,
     unreadMessagesCount: Int,
     eventType: String = "metrics-event") extends MessageEventOut
}
