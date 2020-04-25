package com.lpc.database.meta

import java.sql.Timestamp

package object mappings {
  import ExtendedPostgresDriver.api._

  class DialogTable(tag: Tag) extends Table[DialogEntity](tag, "dialog") {
    def id = column[Option[String]]("id", O.PrimaryKey)
    def name = column[Option[String]]("name")

    def * = (id, name) <> (DialogEntity.tupled, DialogEntity.unapply)
  }

  class DialogParticipantTable(tag: Tag) extends Table[DialogParticipantEntity](tag, "dialog_participant") {
    def id = column[Option[String]]("id", O.PrimaryKey)
    def dialogId = column[String]("dialog_id")
    def userId = column[String]("user_id")

    def * = (id, dialogId, userId) <> (DialogParticipantEntity.tupled, DialogParticipantEntity.unapply)
  }

  class DialogMessageTable(tag: Tag) extends Table[DialogMessageEntity](tag, "dialog_message") {
    def id = column[Option[String]]("id", O.PrimaryKey)
    def dialogId = column[String]("dialog_id")
    def createdTs = column[Option[Timestamp]]("created_ts")
    def content = column[String]("content")
    def senderId = column[String]("sender_id")

    def * = (id, dialogId, createdTs, content, senderId) <> (DialogMessageEntity.tupled, DialogMessageEntity.unapply)
  }
}
