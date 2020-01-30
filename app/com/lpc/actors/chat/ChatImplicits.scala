package com.lpc.actors.chat

import play.api.libs.json.{Format, Json}
import play.api.mvc.WebSocket.MessageFlowTransformer

object ChatImplicits {
  implicit val inEventFormat: Format[ChatInEvent] = Json.format
  implicit val outEventFormat: Format[ChatOutEvent] = Json.format
  implicit val newMessageEventFormat: Format[ChatNewMessageEvent] = Json.format
  implicit val sentMessageEventFormat: Format[ChatSentMessageEvent] = Json.format

  implicit val messageFlowTransformer: MessageFlowTransformer[ChatInEvent, ChatOutEvent] =
    MessageFlowTransformer.jsonMessageFlowTransformer[ChatInEvent, ChatOutEvent]
}
