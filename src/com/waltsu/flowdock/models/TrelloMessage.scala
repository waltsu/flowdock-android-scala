package com.waltsu.flowdock.models

import android.content.Context
import android.view.View
import android.view.LayoutInflater
import com.waltsu.flowdock.R
import android.widget.TextView
import com.waltsu.flowdock.utils
import org.json.JSONObject

class TrelloMessage(override val event: String,
					 override val content: String,
					 override val id: String = "",
					 override val sent: Long = 0,
					 override val userName: String = "")
	  extends FlowMessage(event, content, id, sent, userName) {
  
  override def canBeShown = true
  
  override def getView(c: Context): View = {
    val view = LayoutInflater.from(c).inflate(R.layout.trello_message_item, null)
    val header = view.findViewById(R.id.trelloHeader).asInstanceOf[TextView]
    val body = view.findViewById(R.id.trelloBody).asInstanceOf[TextView]
    val headerContent = getHeaderContent
    val bodyContent = getBodyContent
    header.setText(headerContent)
    body.setText(bodyContent)
    view
  }
  
  def getHeaderContent = {
    val trelloMap = utils.JSONObjectToMap(new JSONObject(content))
    val actionMap = utils.getMapFromOptionJSON(trelloMap.get("action"))
    val actionType = utils.getStringOrEmpty(actionMap, "type")
    actionType match {
      case "updateCard" => getUpdateCardHeader(actionMap) + timeRepresentation
      case "createCard" => getCreateCardHeader(actionMap) + timeRepresentation
      case "commentCard" => getCommentCardHeader(actionMap)
      case _ => getCommonHeader(actionMap)
    }
  }
  
  def getBodyContent = {
    val trelloMap = utils.JSONObjectToMap(new JSONObject(content))
    val actionMap = utils.getMapFromOptionJSON(trelloMap.get("action"))
    val actionType = utils.getStringOrEmpty(actionMap, "type")
    getCommonBody(actionMap)
  }
  
  def getUpdateCardHeader(actionMap: Map[String, Any]) = {
    val dataMap = utils.getMapFromOptionJSON(actionMap.get("data"))
    val cardMap = utils.getMapFromOptionJSON(dataMap.get("card"))
    // If card is moved, listBefore and listAfter are present. Else just old
    val listBeforeMap = utils.getMapFromOptionJSON(dataMap.get("listBefore"))
    val listAfterMap = utils.getMapFromOptionJSON(dataMap.get("listAfter"))
    val oldMap = utils.getMapFromOptionJSON(dataMap.get("old"))

    val cardName = utils.getStringOrEmpty(cardMap, "name")
    val cardClosed = utils.getStringOrEmpty(cardMap, "closed")
    val beforeListName = utils.getStringOrEmpty(listBeforeMap, "name")
    val afterListName = utils.getStringOrEmpty(listAfterMap, "name")
    val oldName = utils.getStringOrEmpty(oldMap, "name")
    if (!oldName.isEmpty())
      "Renamed card '" + oldName + "' to '" + cardName + "'"
    else if (!beforeListName.isEmpty())
      "Moved card '" + cardName + "' from '" + beforeListName + "' to '" + afterListName + "'"
    else if (!cardClosed.isEmpty())
      "Closed card '" + cardName + "'"
    else
      "Updated card '" + cardName + "' (Somehow)"
  }
  def getCreateCardHeader(actionMap: Map[String, Any]) = {
    val dataMap = utils.getMapFromOptionJSON(actionMap.get("data"))
    val cardMap = utils.getMapFromOptionJSON(dataMap.get("card"))
    val listMap = utils.getMapFromOptionJSON(dataMap.get("list"))
    val cardName = utils.getStringOrEmpty(cardMap, "name")
    val listName = utils.getStringOrEmpty(listMap, "name")
    "Added card '" + cardName + "' to '" + listName + "'"
  }
  def getCommentCardHeader(actionMap: Map[String, Any]) = {
    val dataMap = utils.getMapFromOptionJSON(actionMap.get("data"))
    val cardMap = utils.getMapFromOptionJSON(dataMap.get("card"))
    val cardName = utils.getStringOrEmpty(cardMap, "name")
    val text = utils.getStringOrEmpty(dataMap, "text")
    "Commented card '" + cardName + "'" + timeRepresentation + ":\n" + text
  }
  def getCommonHeader(actionMap: Map[String, Any]) = {
    val actionType = utils.getStringOrEmpty(actionMap, "type")
    val dataMap = utils.getMapFromOptionJSON(actionMap.get("data"))
    val cardMap = utils.getMapFromOptionJSON(dataMap.get("card"))
    val cardName = utils.getStringOrEmpty(cardMap, "name")
    actionType + "-event triggered from card '" + cardName + "'"
  }
  
  def getCommonBody(actionMap: Map[String, Any]) = {
    val memberMap = utils.getMapFromOptionJSON(actionMap.get("memberCreator"))
    val dataMap = utils.getMapFromOptionJSON(actionMap.get("data"))
    val boardMap = utils.getMapFromOptionJSON(dataMap.get("board"))
    val boardName = utils.getStringOrEmpty(boardMap, "name")
    val userName = utils.getStringOrEmpty(memberMap, "fullName")
    userName + " (" + boardName + ")"
  }
}