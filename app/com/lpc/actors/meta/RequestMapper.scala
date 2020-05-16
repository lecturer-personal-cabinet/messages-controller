package com.lpc.actors.meta

import com.lpc.actors.meta.actors.{MessageEvent, MessageEventIn, MetricsEventRequest, SocketRequest}

object RequestMapper {
  import ModelImplicits._

  def getEvent(inputEvent: SocketRequest): Option[MessageEventIn] = {
    println(s"Get event: $inputEvent")
    inputEvent.eventType match {
      case "new-message" => inputEvent.data.validateOpt[MessageEvent].asOpt.flatten
      case "metrics-event-request" => inputEvent.data.validateOpt[MetricsEventRequest].asOpt.flatten
    }
  }
}
