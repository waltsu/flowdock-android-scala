package com.waltsu.flowdock.models

import com.waltsu.flowdock.ApplicationState
import android.util.Log

object ModelBuilders {

  // TODO: Better handling of Any
  def constructFlow(m: Map[String, Any]): Flow = {
    val name = m.get("name").get.toString
    val apiUrl = m.get("url").get.toString
    new Flow(name, apiUrl)
  }
  def constructFlowMessage(m: Map[String, Any]): FlowMessage = {
    val event = m.get("event").get.toString
	val sent = m.get("sent").get.asInstanceOf[Long]
	val content = m.get("content").get.toString
	val id = m.get("id").get.toString
	val userId = m.get("user").get.toString
	val user = ApplicationState.currentUsers.find((u: User) => u.id == userId) match {
      case Some(u) => u.name
      case None => ""
    }

    event match {
      case "comment" => new CommentMessage(event, content, id, sent, user)
      case "mail" => new MailMessage(event, content, id, sent, user)
      case "vcs" => new VCSMessage(event, content, id, sent, user)
      case "status" => new StatusMessage(event, content, id, sent, user)
      case "trello" => new TrelloMessage(event, content, id, sent, user)
      case _ => new FlowMessage(event, content, id, sent, user)
    }
  }
  
  def constructUser(m: Map[String, Any]): User = {
    val userId = m.get("id").get.toString
    val name = m.get("name").get.toString
    new User(userId, name)
  }
}