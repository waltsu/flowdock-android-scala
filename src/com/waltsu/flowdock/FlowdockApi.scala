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

// TODO: Some sort of cache
object FlowdockApi {
  val client: AsyncHttpClient = new AsyncHttpClient()

  val baseUrl = "https://api.flowdock.com"
  var currentUsers = List[User]()
    
  client.setBasicAuth(ApplicationState.apiToken, "")
  client.addHeader("Accept", "application/json")
  client.addHeader("Content-Type", "application/json")
  
  def getUsers(): Future[List[User]] = {
    val usersPromise = promise[List[User]] 
    getRequest(baseUrl + "/users", (res) => {
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

  def getMessages(flowUrl: String, cb: (Option[List[FlowMessage]]) => Unit) = {
    getRequest(flowUrl + "/messages", (res) => {
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

  def getFlows(): Future[List[Flow]] = {
    val flowPromise = promise[List[Flow]]
    getRequest(baseUrl + "/flows", (res) => {
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

  private def getRequest(resource: String, cb: (Option[String]) => Unit) = {
    Log.v("debug", "Getting: " + resource)
    client.get(resource, new AsyncHttpResponseHandler() {
    	override def onSuccess(response: String) = {
    	  Log.v("debug", "Got response: " + response)
    	  cb(Some(response))
    	}
    	override def onFailure(throwable: Throwable, error: String) = {
    	  Log.v("debug", "Throwable: " + throwable.toString())
    	  Log.v("debug", "Error: " + error)
    	  cb(None)
    	}
    })
  }

}