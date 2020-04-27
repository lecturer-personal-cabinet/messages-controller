package com.lpc.actors.meta

import com.lpc.actors.meta.actors.{JoinChannelEvent, MessageEventIn, MetricsEventRequest, SendMessageEvent, SendNotificationEvent, SocketRequest}

object RequestMapper {
  import ModelImplicits._

  def getEvent(inputEvent: SocketRequest): Option[MessageEventIn] = {
    println(s"Get event: $inputEvent")
    inputEvent.eventType match {
      case "join-event" => inputEvent.data.validateOpt[JoinChannelEvent].asOpt.flatten
      case "send-notification-event" => inputEvent.data.validateOpt[SendNotificationEvent].asOpt.flatten
      case "send-message-event" => inputEvent.data.validateOpt[SendMessageEvent].asOpt.flatten
      case "metrics-event-request" => inputEvent.data.validateOpt[MetricsEventRequest].asOpt.flatten
    }
  }
}
