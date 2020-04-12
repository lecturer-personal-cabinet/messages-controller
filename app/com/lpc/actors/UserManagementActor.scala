package com.lpc.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.{ActorRefRoutee, BroadcastRoutingLogic, Router}
import akka.util.Timeout
import com.lpc.actors.meta.{RequestMapper, UsersEventBus}
import com.lpc.actors.meta.actors.{JoinChannelEvent, NotificationEvent, SendNotificationEvent, SocketRequest}
import com.lpc.services.messages.MessagesService

import scala.concurrent.ExecutionContext
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
  implicit val timeout: Timeout = Timeout(10 seconds)

  override def receive: Receive = {
    case msg: NotificationEvent => handleNotificationEvent(msg)

    case msg: JoinChannelEvent => handleJoinChannelEvent(msg)
    case msg: SendNotificationEvent => handleSendNotificationEvent(msg)
    case rawEvent: SocketRequest => RequestMapper.getEvent(rawEvent) match {
      case Some(event) =>
        println(s"Event received: $event")
        self ! event
      case None => Unit
    }
    case _ => Unit
  }

  private def handleJoinChannelEvent(msg: JoinChannelEvent): Unit = {
    println(s"New user joined: ${msg.userId}")
    ParticipantsStorage.addParticipant(msg.userId, out)
    ParticipantsStorage.publish(NotificationEvent(msg.userId, "Welcome back, senior!"), msg.userId)
  }

  private def handleSendNotificationEvent(msg: SendNotificationEvent): Unit = {
    println(s"Send notification event: $msg")
    msg.receivers
      .map(uid => NotificationEvent(uid, msg.content))
      .foreach(event => ParticipantsStorage.publish(event, event.userId))
  }

  private def handleNotificationEvent(msg: NotificationEvent): Unit = {
    println(s"Notification event: $msg")
    out ! msg
  }
}