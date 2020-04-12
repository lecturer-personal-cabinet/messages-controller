package com.lpc.actors.meta

import akka.actor.ActorRef
import akka.event.{EventBus, LookupClassification}
import com.lpc.actors.meta.actors.MessageEventOut

class UsersEventBus extends EventBus with LookupClassification {
  type Event = MessageEventOut
  type Classifier = String
  type Subscriber = ActorRef

  override protected def mapSize(): Int = 128

  override protected def compareSubscribers(a: ActorRef, b: ActorRef): Int = a.compareTo(b)

  override protected def classify(event: MessageEventOut): String = event.userId

  override protected def publish(event: MessageEventOut, subscriber: ActorRef): Unit = subscriber ! event
}