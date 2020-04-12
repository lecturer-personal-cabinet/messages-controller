package com.lpc.database.meta

import java.sql.Timestamp

case class DialogEntity(id: Option[String], name: Option[String])

case class DialogParticipantEntity(id: Option[String], dialogId: String, userId: String)

case class DialogMessageEntity(id: Option[String], dialogId: String, createdTs: Option[Timestamp], content: String)
