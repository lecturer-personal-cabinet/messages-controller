package com.lpc.actors

import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import akka.pattern.pipe
import com.lpc.actors.meta.{RequestMapper, UsersEventBus}
import com.lpc.actors.meta.actors.{JoinChannelEvent, MessageEventOut, MessageReceivedEvent, MessageSentEvent, NotificationEvent, SendMessageEvent, SendNotificationEvent, SocketRequest}
import com.lpc.services.messages.MessagesService
import com.lpc.services.models.{Dialog, DialogMessage, DialogParticipant}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._


object UserManagementActor {
  def props(out: ActorRef,
            lookupBus: UsersEventBus,
            messagesService: MessagesService)
           (implicit ec: ExecutionContext): Props = {
    Props(new UserManagementActor(out, lookupBus, messagesService))
  }
}

class UserManagementActor(out: ActorRef,
                          lookupBus: UsersEventBus,
                          messagesService: MessagesService)
                         (implicit ec: ExecutionContext)
  extends Actor {
  import cats.implicits._

  implicit val timeout: Timeout = Timeout(10 minutes)

  override def receive: Receive = {
    case msg: NotificationEvent => fireMessage(msg)
    case msg: MessageSentEvent => fireMessage(msg)
    case msg: MessageReceivedEvent => fireMessage(msg)

    case msg: JoinChannelEvent => handleJoinChannelEvent(msg)
    case msg: SendNotificationEvent => handleSendNotificationEvent(msg)
    case msg: SendMessageEvent => Await.result(handleSendMessageEvent(msg), Duration.Inf)

    case rawEvent: SocketRequest => RequestMapper.getEvent(rawEvent) match {
      case Some(event) =>
        println(s"GOT EVENT: ${event}")
        self ! event
      case None => Unit
    }

    case _ => Unit
  }

  private def handleJoinChannelEvent(msg: JoinChannelEvent): Unit = {
    println(s"New user joined: ${msg.userId}")
    ParticipantsStorage.addParticipant(msg.userId, out)
  }

  private def handleSendNotificationEvent(msg: SendNotificationEvent): Unit = {
    println(s"Send notification event: $msg")
    msg.receivers
      .map(uid => NotificationEvent(uid, msg.content))
      .foreach(event => ParticipantsStorage.publish(event, event.userId))
  }

  private def handleSendMessageEvent(msg: SendMessageEvent) = {
    println(s"Handle send message event: ${msg}")
    println(s"SenderID: ${msg.userId}")

    val participants = msg.receivers ++ Seq(msg.userId)
    println(s"Participants: ${participants}")
    val result = for {
      dialog <- messagesService.findDialog(participants)
        .leftFlatMap(_ => messagesService.insertIfNotExistsDialog(Dialog(id = None, name = None)))
      participantsResult <- messagesService.insertIfNotExistsDialogParticipants(
        participants.map(id => DialogParticipant(None, dialog.id.get, id)))
      messageResult <- messagesService.insertOrUpdateMessage(DialogMessage(
        id = None,
        dialogId = dialog.id.get,
        createdTs = Option(LocalDateTime.now()),
        content = msg.content,
        senderId = msg.userId))
      _ = msg.receivers
        .map(uid => MessageReceivedEvent(userId = uid, message = messageResult))
        .foreach(event => ParticipantsStorage.publish(event, event.userId))
    } yield {
      println(s"Dialog result: ${""}")
      println(s"Participant result: ${participantsResult}")
      println(s"Message result: ${messageResult}")
      fireMessage(MessageSentEvent(msg.userId))
    }

    result.value
  }

  private def fireMessage(event: MessageEventOut) = {
    println(s"Fire message request: $event")
    out ! event
  }
}