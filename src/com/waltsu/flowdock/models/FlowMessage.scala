package com.waltsu.flowdock.models

import com.waltsu.flowdock.utils
import org.json.JSONObject
import android.util.Log
import android.view.View
import android.view.LayoutInflater
import com.waltsu.flowdock.R
import android.content.Context
import android.widget.TextView

class FlowMessage(val event: String, 	
				  val content: String,
				  val sent: Long = 0,
				  val userName: String = "") {
  
  
  def getView(c: Context): View = {
    val view = LayoutInflater.from(c).inflate(R.layout.content_list_item, null)
    val textView = view.findViewById(R.id.contentText).asInstanceOf[TextView]
    textView.setText(getContent)
    view
  }
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
    userName + ": " + body
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