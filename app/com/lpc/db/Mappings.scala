package com.lpc.db

import java.sql.Timestamp

import com.lpc.db.Models.{DialogEntity, DialogId, DialogMessageEntity, DialogMessageId}

object Mappings {
  import ExtendedPostgresDriver.api._

  class DialogTable(tag: Tag) extends Table[DialogEntity](tag, "dialog") {
    def chatId = column[Option[DialogId]]("chat_id", O.PrimaryKey)
    def participants = column[List[String]]("participant")

    def * = (chatId, participants) <> (DialogEntity.tupled, DialogEntity.unapply)
  }

  class DialogMessageTable(tag: Tag) extends Table[DialogMessageEntity](tag, "dialog_message") {
    def messageId = column[Option[DialogMessageId]]("message_id", O.PrimaryKey)
    def dialogId = column[DialogId]("dialog_id")
    def sender = column[String]("sender")
    def content = column[String]("content")

    def * = (messageId, dialogId, sender, content) <> (DialogMessageEntity.tupled, DialogMessageEntity.unapply)
  }
}
