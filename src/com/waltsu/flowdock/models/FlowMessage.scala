package com.waltsu.flowdock.models

import com.waltsu.flowdock.utils
import org.json.JSONObject
import android.util.Log
import android.view.View
import android.view.LayoutInflater
import com.waltsu.flowdock.R
import android.content.Context
import android.widget.TextView
import java.util.Date
import java.text.SimpleDateFormat

class FlowMessage(val event: String, 	
				  val content: String,
				  val id: String = "",
				  val sent: Long = 0,
				  val userName: String = "") {
  
  
  def getView(c: Context): View = {
    event match {
      case "comment" => getCommentView(c)
      case _ => getDefaultView(c)
    }
  }
  
  def getCommentView(c: Context): View = {
    val view = LayoutInflater.from(c).inflate(R.layout.comment_message_item, null)
    val header = view.findViewById(R.id.commentHeader).asInstanceOf[TextView]
    val body = view.findViewById(R.id.commentBody).asInstanceOf[TextView]
    val title = view.findViewById(R.id.commentTitle).asInstanceOf[TextView]
    header.setText(userName + timeRepresentation + ":")
    body.setText(getCommentContent._2)
    title.setText(getCommentContent._1)
    view
  }
  def getDefaultView(c: Context): View = {
    val view = LayoutInflater.from(c).inflate(R.layout.content_list_item, null)
    val header = view.findViewById(R.id.contentHeader).asInstanceOf[TextView]
    val body = view.findViewById(R.id.contentBody).asInstanceOf[TextView]
    header.setText(userName + timeRepresentation + ":")
    body.setText(getDefaultContent)
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

  def getCommentContent = {
    val contentMap = utils.JSONObjectToMap(new JSONObject(content)) 
    val title = contentMap.get("title") match {
      case Some(x) => x.toString
      case None => ""
    }
    val commentContent = contentMap.get("text") match {
      case Some(x) => x.toString
      case None => ""
    }
    (title, commentContent)
    
  }
  def getDefaultContent: String = {
    val body = event match {
      case "message" => content
      case "status" => content
      case _ => "Not implemented :( ("  + event + ")"
    }
    body
  }
  
  
  def timeRepresentation: String =
    sent match {
	  case 0 => ""
	  case t => " (" + new SimpleDateFormat("HH:mm").format(new Date(t)) + ")"
    }
    


}