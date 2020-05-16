package com.lpc.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import com.lpc.actors.meta.RequestMapper
import com.lpc.actors.meta.actors.{MessageEventOut, MessageReceivedEvent, MessageSentEvent, MetricsEvent, MetricsEventRequest, NotificationEvent, SocketRequest}
import com.lpc.services.messages.MessagesService

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._


object UserManagementActor {
  def props(out: ActorRef,
            messagesService: MessagesService)
           (implicit ec: ExecutionContext): Props = {
    Props(new UserManagementActor(out, messagesService))
  }
}

class UserManagementActor(out: ActorRef,
                          messagesService: MessagesService)
                         (implicit ec: ExecutionContext)
  extends Actor {
  import cats.implicits._

  implicit val timeout: Timeout = Timeout(10 minutes)

  override def receive: Receive = {
    case msg: NotificationEvent => fireMessage(msg)
    case msg: MessageSentEvent => fireMessage(msg)
    case msg: MessageReceivedEvent => fireMessage(msg)
    case msg: MetricsEvent => fireMessage(msg)

    case msg: MetricsEventRequest => Await.result(handleMetricsEventRequest(msg), Duration.Inf)

    case rawEvent: SocketRequest => RequestMapper.getEvent(rawEvent) match {
      case Some(event) =>
        println(out)
        println(s"GOT EVENT: ${event}")
        self ! event
      case None => Unit
    }

    case _ => Unit
  }
  private def handleMetricsEventRequest(msg: MetricsEventRequest) = {
    println(s"Handler metrics event request: ${msg}")

    messagesService.getUnreadMessages(msg.userId)
      .map(number => fireMessage(MetricsEvent(msg.userId, number)))
      .value
  }

  private def fireMessage(event: MessageEventOut) = {
    println(s"Fire message request: $event")
    out ! event
  }
}