package com.lpc.services

import java.time.LocalDateTime

import play.api.libs.json.{Format, Json}

package object models {
  case class DialogMessage(id: Option[String], dialogId: String, createdTs: Option[LocalDateTime], content: String, senderId: String, isRead: Boolean)
  object DialogMessage {
    implicit val fmt: Format[DialogMessage] = Json.format
  }

  case class Dialog(id: Option[String], name: Option[String])
  object Dialog {
    implicit val fmt: Format[Dialog] = Json.format
  }

  case class DialogParticipant(id: Option[String], dialogId: String, userId: String)
  object DialogParticipant {
    implicit val fmt: Format[DialogParticipant] = Json.format
  }
}
