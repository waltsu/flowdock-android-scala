package com.waltsu.flowdock.models

import android.content.Context
import android.view.View
import android.widget.TextView
import android.view.LayoutInflater
import com.waltsu.flowdock.utils
import com.waltsu.flowdock.R
import org.json.JSONObject

class CommentMessage(override val event: String,
					 override val content: String,
					 override val id: String = "",
					 override val sent: Long = 0,
					 override val userName: String = "")
	  extends FlowMessage(event, content, id, sent, userName) {

  override def getView(c: Context): View = {
    val view = LayoutInflater.from(c).inflate(R.layout.comment_message_item, null)
    val header = view.findViewById(R.id.commentHeader).asInstanceOf[TextView]
    val body = view.findViewById(R.id.commentBody).asInstanceOf[TextView]
    val title = view.findViewById(R.id.commentTitle).asInstanceOf[TextView]
    header.setText(userName + timeRepresentation + ":")
    body.setText(getCommentContent._2)
    title.setText(getCommentContent._1)
    view
  }

  def getCommentContent = {
    val contentMap = utils.JSONObjectToMap(new JSONObject(content)) 
    val title = utils.getStringOrEmpty(contentMap, "title")
    val commentContent = utils.getStringOrEmpty(contentMap, "text")
    (title, commentContent)
    
  }

}