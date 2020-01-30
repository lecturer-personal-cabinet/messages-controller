package com.lpc.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.pipe
import akka.util.Timeout
import cats.effect.IO
import com.lpc.actors.chat.{ChatInEvent, ChatNewMessageEvent, LookupBus}
import com.lpc.db.Models.DialogId
import com.lpc.services.dialog.DialogService
import com.lpc.services.messages.{DialogMessage, DialogMessageService}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object DialogActor {
  def props(out: ActorRef,
            lookupBus: LookupBus,
            dialogService: DialogService[IO, DBIO],
            dialogMessageService: DialogMessageService[IO, DBIO])
           (implicit ec: ExecutionContext) =
    Props(new DialogActor(out,
      lookupBus,
      dialogService,
      dialogMessageService))
}

class DialogActor(out: ActorRef,
                  lookupBus: LookupBus,
                  dialogService: DialogService[IO, DBIO],
                  dialogMessageService: DialogMessageService[IO, DBIO])
                 (implicit ec: ExecutionContext)
  extends Actor {

  implicit val timeout: Timeout = Timeout(10 seconds)

  def receive = {
    case msg: ChatInEvent =>
      println("Chat in: " + msg)
      handleChatInEvent(out, msg, lookupBus)
        .unsafeToFuture()
        .pipeTo(out)
    case msg: ChatNewMessageEvent => out ! msg
    case _ => Unit
  }

  private[this] def handleChatInEvent(out: ActorRef, msg: ChatInEvent, lookupBus: LookupBus) = {
    if(!lookupBus.isAlreadySubscribed(msg.dialogId, out)) {
      lookupBus.subscribe(out, msg.dialogId)
    }

    val dialogMessage = DialogMessage(
      None,
      DialogId(msg.dialogId),
      msg.senderId,
      msg.message)

    val chatOutEvent = ChatNewMessageEvent(msg.dialogId, msg.message, msg.senderId)

    dialogMessageService.insertDialogMessage(dialogMessage)
      .map(_ => lookupBus.publish(chatOutEvent))
      .map(_ => chatOutEvent)
  }
}
