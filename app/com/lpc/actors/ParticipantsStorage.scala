package com.lpc.actors

import akka.actor.ActorRef
import com.lpc.actors.meta.actors.{MessageEventOut, NotificationEvent}

object ParticipantsStorage {
  private var Participants: Map[String, ActorRef] = Map.empty[String, ActorRef]

  def addParticipant(userId: String, actor: ActorRef): Unit = {
    Participants += userId -> actor
    println(s"Participant added. Size: ${Participants.size}")
  }

  def removeParticipant(userId: String): Unit = {
    Participants -= userId
    println(s"Participant removed. Size: ${Participants.size}")
  }

  def getParticipant(userId: String): Option[ActorRef] = Participants.get(userId)

  def publish(event: MessageEventOut, userId: String): Unit = {
    println(s"Publish: $userId, $event")
    getParticipant(userId).foreach(actor => {
      println(s"Send event: $event, userId: $userId")
      actor ! event
    })
  }
}
