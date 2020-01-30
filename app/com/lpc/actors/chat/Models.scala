package com.lpc.actors.chat

sealed trait ChatOutEvent {
  def dialogId: String
}
case class ChatInEvent(dialogId: String, senderId: String, message: String)
case class ChatNewMessageEvent(dialogId: String, message: String, senderId: String) extends ChatOutEvent