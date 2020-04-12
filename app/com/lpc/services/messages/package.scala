package com.lpc.services

import java.time.LocalDateTime

import play.api.libs.json.{Format, Json}

package object models {
  case class DialogMessage(id: Option[String], dialogId: String, createdTs: Option[LocalDateTime], content: String)
  object DialogMessage {
    implicit val fmt: Format[DialogMessage] = Json.format
  }
}
