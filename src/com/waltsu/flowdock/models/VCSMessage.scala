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
      case "pull_request" => getPullRequestAuthor(vcsMap)
      case _ => ""
    }
  }
  
  def getContentContent = {
    val vcsMap = utils.JSONObjectToMap(new JSONObject(content))
    val event = utils.getStringOrEmpty(vcsMap, "event")
    event match {
      case "push" => getPushContent(vcsMap)
      case "issues" => getIssuesContent(vcsMap)
      case "issue_comment" => getIssueCommentContent(vcsMap)
      case "pull_request" => getPullRequestContent(vcsMap)
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
    val issueTitle = utils.getStringOrEmpty(issueMap, "title")
    name + " " + action + " an issue #" + number + 
    " (" + issueTitle + ")" + timeRepresentation
  }
  def getIssueCommentAuthor(vcsMap: Map[String, Any]) = {
    val senderMap = utils.getMapFromOptionJSON(vcsMap.get("sender"))
    val issueMap = utils.getMapFromOptionJSON(vcsMap.get("issue"))

    val name = utils.getStringOrEmpty(senderMap, "login")
    val number = utils.getStringOrEmpty(issueMap, "number")
    val issueTitle = utils.getStringOrEmpty(issueMap, "title")
    name + " commented on issue #" + number + 
    " (" + issueTitle + ")" + timeRepresentation
  }
  def getPullRequestAuthor(vcsMap: Map[String, Any]) = {
    val senderMap = utils.getMapFromOptionJSON(vcsMap.get("sender"))
    val prMap = utils.getMapFromOptionJSON(vcsMap.get("pull_request"))

    val name = utils.getStringOrEmpty(senderMap, "login")
    val prTitle = utils.getStringOrEmpty(prMap, "title")
    val number = utils.getStringOrEmpty(vcsMap, "number")
    val action = utils.getStringOrEmpty(vcsMap, "action")
    name + " " + action + " a pull request #" + number +
    " (" + prTitle + ")" + timeRepresentation
    
  }

  def getPushContent(vcsMap: Map[String, Any]) = {
    val headCommitMap = utils.getMapFromOptionJSON(vcsMap.get("head_commit"))
    utils.getStringOrEmpty(headCommitMap, "message")
  }
  def getIssuesContent(vcsMap: Map[String, Any]) = {
    val issueMap = utils.getMapFromOptionJSON(vcsMap.get("issue"))
    utils.getStringOrEmpty(issueMap, "body")
  }
  def getIssueCommentContent(vcsMap: Map[String, Any]) = {
    val commentMap = utils.getMapFromOptionJSON(vcsMap.get("comment"))
    utils.getStringOrEmpty(commentMap, "body")

  }
  def getPullRequestContent(vcsMap: Map[String, Any]) = {
    val prMap = utils.getMapFromOptionJSON(vcsMap.get("pull_request"))
    utils.getStringOrEmpty(prMap, "body")
  }

}