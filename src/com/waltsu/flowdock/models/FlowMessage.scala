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
import android.webkit.WebView

class FlowMessage(val event: String, 	
				  val content: String,
				  val id: String = "",
				  val sent: Long = 0,
				  val userName: String = "") {
  
  
  def getView(c: Context): View = {
    event match {
      case "comment" => getCommentView(c)
      case "mail" => getMailView(c)
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
  def getMailView(c: Context): View = {
    val view = LayoutInflater.from(c).inflate(R.layout.mail_message_item, null)
    val subject = view.findViewById(R.id.mailSubject).asInstanceOf[TextView]
    val contentWebView = view.findViewById(R.id.mailContent).asInstanceOf[TextView]
    val source = view.findViewById(R.id.mailSource).asInstanceOf[TextView]
    val mailContent = getMailContent
    subject.setText(mailContent._1 + timeRepresentation + ":")
    contentWebView.setText(mailContent._2)
    contentWebView.setBackgroundColor(0x00000000)
    source.setText(mailContent._3)
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

  def getCommentContent = {
    val contentMap = utils.JSONObjectToMap(new JSONObject(content)) 
    val title = utils.getStringOrEmpty(contentMap, "title")
    val commentContent = utils.getStringOrEmpty(contentMap, "text")
    (title, commentContent)
    
  }

  def getMailContent = {
    val mailMap = utils.JSONObjectToMap(new JSONObject(content))
    val subject = utils.getStringOrEmpty(mailMap, "subject")
    val mailContent = android.text.Html.fromHtml(utils.getStringOrEmpty(mailMap, "content")).toString()
    val source = utils.getStringOrEmpty(mailMap, "source")
    (subject, mailContent, source)
  }
  def getDefaultContent: String = {
    val body = event match {
      case "message" => content
      case "status" => content
      case _ => "Not implemented :( ("  + event + ")"
    }
    body
  }
  
  def canBeShown: Boolean = {
    event match {
    case "message" => true
    case "status" => true
    case "comment" => true
    case "mail" => true
    case _ => false
	}
  }

  
  def timeRepresentation: String =
    sent match {
	  case 0 => ""
	  case t => " (" + new SimpleDateFormat("HH:mm").format(new Date(t)) + ")"
    }
    


}