package com.waltsu.flowdock.models

import android.widget.TextView
import android.view.LayoutInflater
import android.content.Context
import android.view.View
import com.waltsu.flowdock.R
import com.waltsu.flowdock.utils
import org.json.JSONObject

class MailMessage(override val event: String,
					 override val content: String,
					 override val id: String = "",
					 override val sent: Long = 0,
					 override val userName: String = "")
	  extends FlowMessage(event, content, id, sent, userName) {

  override def canBeShown = true

  override def getView(c: Context): View = {
    val view = LayoutInflater.from(c).inflate(R.layout.mail_message_item, null)
    val subject = view.findViewById(R.id.mailSubject).asInstanceOf[TextView]
    val contentView = view.findViewById(R.id.mailContent).asInstanceOf[TextView]
    val source = view.findViewById(R.id.mailSource).asInstanceOf[TextView]
    val mailContent = getMailContent
    subject.setText(mailContent._1 + timeRepresentation)
    contentView.setText(mailContent._2)
    source.setText(mailContent._3)
    view
  }

  def getMailContent = {
    val mailMap = utils.JSONObjectToMap(new JSONObject(content))
    val fromMap = utils.getMapFromOptionJSON(Some(utils.getListFromOptionJSON(mailMap.get("from")).head))

    val subject = utils.getStringOrEmpty(mailMap, "subject")
    val mailContent = android.text.Html.fromHtml(utils.getStringOrEmpty(mailMap, "content")).toString()
    
    val fromMail = utils.getStringOrEmpty(fromMap, "address")
    val fromName = utils.getStringOrEmpty(fromMap, "name")
    if (fromName.length() > 0)
      (subject, mailContent, fromName + " (" + fromMail + ")")
    else
      (subject, mailContent, fromMail)
      
  }

}