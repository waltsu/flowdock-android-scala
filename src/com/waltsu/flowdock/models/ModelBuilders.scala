package com.waltsu.flowdock.models

object ModelBuilders {

  def constructFlowMessage(m: Map[String, Any]): FlowMessage = {
    val event = m.get("event")
	val sent = m.get("sent")
	val content = m.get("content")
	val user = m.get("user")
	new FlowMessage(event.get.toString,
					sent.get.asInstanceOf[Long],
			  		content.get.toString,
			  		user.get.toString)
	}
  
}