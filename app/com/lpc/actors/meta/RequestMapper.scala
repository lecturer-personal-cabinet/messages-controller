package com.lpc.actors.meta

import com.lpc.actors.meta.actors.{JoinChannelEvent, MessageEventIn, SocketRequest}

object RequestMapper {
  import ModelImplicits._

  def getEvent(inputEvent: SocketRequest): Option[MessageEventIn] = {
    println(s"Get event: $inputEvent")
    inputEvent.eventType match {
      case "join-event" => inputEvent.data.validateOpt[JoinChannelEvent].asOpt.flatten
    }
  }
}
