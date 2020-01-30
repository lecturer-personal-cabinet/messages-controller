package com.lpc.actors.chat

import akka.actor.ActorRef
import akka.event.{EventBus, LookupClassification}
import akka.util.Index

trait LookupBus extends EventBus with LookupClassification {
  type Event = ChatOutEvent
  type Classifier = String
  type Subscriber = ActorRef

  def isAlreadySubscribed(dialogId: String, to: Subscriber): Boolean
}

class LookupBusImpl extends LookupBus {
  override protected def classify(event: Event): Classifier = event.dialogId

  override protected def publish(event: Event, subscriber: Subscriber): Unit = subscriber ! event

  override protected def compareSubscribers(a: Subscriber, b: Subscriber): Int = a.compareTo(b)

  override protected def mapSize: Int = 128

  override def isAlreadySubscribed(dialogId: String, to: Subscriber): Boolean =
    this.subscribers.findValue(dialogId)(_ == to).isDefined

}
