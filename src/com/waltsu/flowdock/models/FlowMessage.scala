package com.waltsu.flowdock.models

import com.waltsu.flowdock.utils
import org.json.JSONObject
import android.util.Log

class FlowMessage(val event: String, 	
				  val sent: Long,
				  val content: String,
				  val user: String) {
  
  def canBeShown: Boolean = {
    event match {
    case "message" => true
    case "status" => true
    case "comment" => true
    case _ => false
	}
  }

  def getContent: String = {
    val body = event match {
      case "message" => content
      case "status" => content
      case "comment" => constructComment
      case _ => "Not implemented :( ("  + event + ")"
    }
    user + ": " + body
  }
  
  def constructComment = {
    val contentMap = utils.JSONObjectToMap(new JSONObject(content)) 
    val title = contentMap.get("title") match {
      case Some(x) => x.toString
      case None => ""
    }
    val commentContent = contentMap.get("content") match {
      case Some(x) => x.toString
      case None => ""
    }
    title + ":\n" + commentContent
  }


}