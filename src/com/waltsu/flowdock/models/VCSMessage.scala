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

  override def canBeShown = true

  override def getView(c: Context): View = {
    val view = LayoutInflater.from(c).inflate(R.layout.vcs_message_item, null)
    val author = view.findViewById(R.id.vcsAuthor).asInstanceOf[TextView]
    val contentView = view.findViewById(R.id.vcsContent).asInstanceOf[TextView]
    author.setText(getAuthorContent)
    val contentContent = getContentContent
    if (contentContent.length() == 0)
      contentView.setVisibility(View.GONE)
    else
	  contentView.setText(getContentContent)

    view
  }

  def getAuthorContent = {
    val vcsMap = utils.JSONObjectToMap(new JSONObject(content))
    val event = utils.getStringOrEmpty(vcsMap, "event")
    event match {
      case "push" => getPushAuthor(vcsMap)
      case "issues" => getIssuesAuthor(vcsMap)
      case "issue_comment" => getIssueCommentAuthor(vcsMap)
      case _ => ""
    }
  }
  
  def getContentContent = {
    val vcsMap = utils.JSONObjectToMap(new JSONObject(content))
    val event = utils.getStringOrEmpty(vcsMap, "event")
    event match {
      case "push" => getPushContent(vcsMap)
      case "issues" => getIssuesContent(vcsMap)
      case "issue_comment" => getIssuesContent(vcsMap)
      case _ => ""
    }
  }

  def getPushAuthor(vcsMap: Map[String, Any]) = {
    val pusherMap = utils.getMapFromOptionJSON(vcsMap.get("pusher"))
    val repositoryMap = utils.getMapFromOptionJSON(vcsMap.get("repository"))
    val headCommitMap = utils.getMapFromOptionJSON(vcsMap.get("head_commit"))

    val name = utils.getStringOrEmpty(pusherMap, "name")
    val project = utils.getStringOrEmpty(repositoryMap, "name")
    val headHash = utils.getStringOrEmpty(headCommitMap, "id") match {
      case x if x.length() > 7 => x.substring(0, 7)
      case x: String => x
      case _ => ""
    }

    name + " pushed new head #" + headHash + " to " + project + timeRepresentation
  }
  def getIssuesAuthor(vcsMap: Map[String, Any]) = {
    val senderMap = utils.getMapFromOptionJSON(vcsMap.get("sender"))
    val issueMap = utils.getMapFromOptionJSON(vcsMap.get("issue"))

    val name = utils.getStringOrEmpty(senderMap, "login")
    val action = utils.getStringOrEmpty(vcsMap, "action")
    val number = utils.getStringOrEmpty(issueMap, "number")
    name + " " + action + " an issue #" + number + timeRepresentation
  }
  def getIssueCommentAuthor(vcsMap: Map[String, Any]) = {
    val senderMap = utils.getMapFromOptionJSON(vcsMap.get("sender"))
    val issueMap = utils.getMapFromOptionJSON(vcsMap.get("issue"))
    val name = utils.getStringOrEmpty(senderMap, "login")
    val number = utils.getStringOrEmpty(issueMap, "number")
    name + " commented on issue #" + number + timeRepresentation
  }

  def getPushContent(vcsMap: Map[String, Any]) = {
    val headCommitMap = utils.getMapFromOptionJSON(vcsMap.get("head_commit"))
    val message = utils.getStringOrEmpty(headCommitMap, "message")
    message
  }
  def getIssuesContent(vcsMap: Map[String, Any]) = {
    val issueMap = utils.getMapFromOptionJSON(vcsMap.get("issue"))
    utils.getStringOrEmpty(issueMap, "body")
  }

}