package com.lpc.db

import slick.lifted.MappedTo

object Models {
  case class DialogId(value: String) extends AnyVal with MappedTo[String]
  case class DialogEntity(dialogId: Option[DialogId], participants: List[String])

  case class DialogMessageId(value: String) extends AnyVal with MappedTo[String]
  case class DialogMessageEntity(messageId: Option[DialogMessageId], dialogId: DialogId, sender: String, content: String)
}
