package com.waltsu.flowdock.models

import scala.util.parsing.json.JSON

case class StringAny(k: String, a: Any)
class FlowMessage(val event: String, 	
				  val sent: Long,
				  val content: String) {
  
  def getContent: String = {
    event match {
      case "message" => content
      case "status" => content
      case "comment" => constructComment
      case _ => "Not implemented :(("  + event + ")"
    }
  }
  
  def constructComment = {
    val contentMap = JSON.parseFull(content) match {
      case Some(m) => m.asInstanceOf[Map[String, Any]]
      case None => Map[String, Any]()
    }
    val title = contentMap.get("title") match {
      case Some(x) => x.toString
      case None => ""
    }
    val commentContent = contentMap.get("content") match {
      case Some(x) => x.toString
      case None => ""
    }
    title + ": " + commentContent

  }

}