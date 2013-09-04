package com.waltsu.flowdock.models

import android.widget.TextView
import android.view.LayoutInflater
import android.content.Context
import android.view.View
import com.waltsu.flowdock.R
import com.waltsu.flowdock.utils
import org.json.JSONObject

class VCSMessage(override val event: String,
					 override val content: String,
					 override val id: String = "",
					 override val sent: Long = 0,
					 override val userName: String = "")
	  extends FlowMessage(event, content, id, sent, userName) {

  override def getView(c: Context): View = {
    val view = LayoutInflater.from(c).inflate(R.layout.vcs_message_item, null)
    val header = view.findViewById(R.id.vcsHeader).asInstanceOf[TextView]
    val pusher = view.findViewById(R.id.vcsPusher).asInstanceOf[TextView]
    header.setText("Coming")
    pusher.setText(getVCSAuthorContent)
    view
  }

  def getVCSAuthorContent = {
    val vcsContent = utils.JSONObjectToMap(new JSONObject(content))
    val pusherMap = vcsContent.get("pusher") match {
      case Some(x) => {
        x match {
          case json if json.isInstanceOf[JSONObject] => utils.JSONObjectToMap(json.asInstanceOf[JSONObject])
          case _ => Map[String, Any]()
        }    
      }
      case None => Map[String, Any]()
    }
    val email = utils.getStringOrEmpty(pusherMap, "email")
    val name = utils.getStringOrEmpty(pusherMap, "name")
    name + "(" + email + ")"
  }
}