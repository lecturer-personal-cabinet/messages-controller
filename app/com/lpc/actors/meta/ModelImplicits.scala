package com.lpc.actors.meta

import com.lpc.actors.meta.actors._
import play.api.libs.json.{Format, Json}
import play.api.mvc.WebSocket.MessageFlowTransformer

object ModelImplicits {
  implicit val eventInFormat: Format[MessageEventIn] = Json.format
  implicit val eventOutFormat: Format[MessageEventOut] = Json.format
  implicit val socketRequestFmt: Format[SocketRequest] = Json.format
  implicit val joinEventFormat: Format[JoinChannelEvent] = Json.format
  implicit val sendNotificationEventFormat: Format[SendNotificationEvent] = Json.format
  implicit val notificationEventFormat: Format[NotificationEvent] = Json.format
  implicit val sendMessageEventFormat: Format[SendMessageEvent] = Json.format
  implicit val messageSentEventFmt: Format[MessageSentEvent] = Json.format
  implicit val messageReceivedEventFmt: Format[MessageReceivedEvent] = Json.format

  implicit val flowJoinTransformer: MessageFlowTransformer[SocketRequest, MessageEventOut] =
    MessageFlowTransformer.jsonMessageFlowTransformer[SocketRequest, MessageEventOut]
}
