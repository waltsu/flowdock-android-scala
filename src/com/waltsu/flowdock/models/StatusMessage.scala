package com.waltsu.flowdock.models

import android.content.Context
import android.view.View
import android.view.LayoutInflater
import com.waltsu.flowdock.R
import android.widget.TextView

class StatusMessage(override val event: String,
					 override val content: String,
					 override val id: String = "",
					 override val sent: Long = 0,
					 override val userName: String = "")
	  extends FlowMessage(event, content, id, sent, userName) {
  override def getView(c: Context): View = {
    val view = LayoutInflater.from(c).inflate(R.layout.status_message_item, null)
    val header = view.findViewById(R.id.statusHeader).asInstanceOf[TextView]
    val body = view.findViewById(R.id.statusBody).asInstanceOf[TextView]
    header.setText(userName + timeRepresentation + ":")
    body.setText(content)
    view
  }
}