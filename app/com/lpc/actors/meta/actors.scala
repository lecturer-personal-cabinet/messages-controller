package com.lpc.actors.meta

import play.api.libs.json.JsValue

package object actors {
  sealed trait MessageEventIn {
    def userId: String
  }

  sealed trait MessageEventOut {
    def eventType: String
    def userId: String
  }

  case class JoinChannelEvent(userId: String) extends MessageEventIn

  case class SendNotificationEvent(
    userId: String,
    receivers: Seq[String],
    content: String) extends MessageEventIn

  case class NotificationEvent(
    userId: String,
    content: String,
    eventType: String = "notification-event") extends MessageEventOut

  case class SocketRequest(
    userId: String,
    data: JsValue,
    eventType: String)
}
