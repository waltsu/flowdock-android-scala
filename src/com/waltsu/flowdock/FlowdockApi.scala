package com.waltsu.flowdock

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.Failure
import scala.util.Success
import org.apache.http.HttpResponse
import org.apache.http.auth.AuthScope
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.waltsu.flowdock.models.Flow
import com.waltsu.flowdock.models.FlowMessage
import com.waltsu.flowdock.models.ModelBuilders
import android.util.Log
import com.waltsu.flowdock.models.User
import com.loopj.android.http.RequestParams
import android.content.Context

// TODO: Some sort of cache
object FlowdockApi {
  val baseUrl = "https://api.flowdock.com"
    
  def getUsers(c: Context): Future[List[User]] = {
    val usersPromise = promise[List[User]] 
    RESTClient.getRequest(c, baseUrl + "/users", (res) => {
      res match {
	    case Some(response) => {
          val users = utils.JSONArrayToList(new JSONArray(response))
          val usersList = users.map((k: Any) => {
            k match {
              case x if x.isInstanceOf[JSONObject] => utils.JSONObjectToMap(x.asInstanceOf[JSONObject])
              case _ => Map[String, Any]()
            }
          })
          val userModels = usersList.map((u) => {
            ModelBuilders.constructUser(u)
          })
          usersPromise success userModels
        }
        case None => usersPromise failure new Exception("Error when fetching users")
      }
    })
    usersPromise.future
  } 

  def getMessages(c: Context, flowUrl: String, cb: (Option[List[FlowMessage]]) => Unit) = {
    RESTClient.getRequest(c, flowUrl + "/messages", (res) => {
      res match {
	    case Some(response) => {
	      val messages = utils.JSONArrayToList(new JSONArray(response))
	      val messageList = messages.map((k: Any) => {
	        k match {
	          case x if x.isInstanceOf[JSONObject] => utils.JSONObjectToMap(x.asInstanceOf[JSONObject])
	          case _ => Map[String, Any]()
	        }
	      })
	      val messageModels = messageList.map((m: Map[String, Any]) => {
	        ModelBuilders.constructFlowMessage(m)
	      })
	      cb(Some(messageModels))
	    }
	    case None => cb(None)
      }
    })
  }
  
  def sendMessage(c: Context, flowUrl: String, message: FlowMessage, cb: (Boolean) => Unit) = {
    val messageData = Map("event" -> message.event, "content" -> message.content) 
    RESTClient.postRequest(c, flowUrl + "/messages", messageData, (res) => {
      res match {
        case Some(response) => cb(true)
        case None => cb(false)
      }
    })
  }

  def getFlows(c: Context): Future[List[Flow]] = {
    val flowPromise = promise[List[Flow]]
    RESTClient.getRequest(c, baseUrl + "/flows", (res) => {
      res match {
	    case Some(response) => {
	      val flows = utils.JSONArrayToList(new JSONArray(response))
	      val flowList = flows.map((k: Any) => {
	        k match {
	          case x if x.isInstanceOf[JSONObject] => utils.JSONObjectToMap(x.asInstanceOf[JSONObject])
	          case _ => Map[String, Any]()
	        }
	      })
	      val flowModels = flowList.map((f: Map[String, Any]) => {
	        ModelBuilders.constructFlow(f)
	      })
	      flowPromise success(flowModels)
	    }
	    case None => flowPromise failure new Exception("Error when fetching flows")
      }
    })
    flowPromise.future
  }


}