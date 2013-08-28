package com.waltsu.flowdock.models

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
	val user = m.get("user").get.toString
	new FlowMessage(event, sent, content, user)
	}
  
  def constructUser(m: Map[String, Any]): User = {
    val userId = m.get("id").get.toString
    val name = m.get("name").get.toString
    new User(userId, name)
  }
}