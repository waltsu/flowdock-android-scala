package com.waltsu.flowdock.models

import com.waltsu.flowdock.utils
import org.json.JSONObject
import android.util.Log

class FlowMessage(val event: String, 	
				  val sent: Long,
				  val content: String,
				  val user: String) {
  
  def getContent: String = {
    event match {
      case "message" => content
      case "status" => content
      case "comment" => constructComment
      case "action" => constructAction
      case _ => "Not implemented :( ("  + event + ")"
    }
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
    title + ": " + commentContent
  }
  
  def constructAction = {
    val contentMap = utils.JSONObjectToMap(new JSONObject(content))

    "" 
  }


}