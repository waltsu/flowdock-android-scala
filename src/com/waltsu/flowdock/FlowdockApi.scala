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
  val apiToken = "change"

  val baseUrl = "https://api.flowdock.com"
  var currentUsers = List[User]()
    
  client.setBasicAuth(apiToken, "")
  client.addHeader("Accept", "application/json")
  client.addHeader("Content-Type", "application/json")
  
  def getUsers(): Future[List[User]] = {
    val usersPromise = promise[List[User]] 
    getRequest(baseUrl + "/users") onComplete {
      case Success(response) => {
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
      case Failure(throwable) => usersPromise failure throwable
    }
    usersPromise.future
  } 

  def getMessages(flowUrl: String): Future[List[FlowMessage]] = {
    val messagePromise = promise[List[FlowMessage]]  
    getRequest(flowUrl + "/messages")  onComplete {
      case Success(response) => {
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
        messagePromise success messageModels
      }
      case Failure(throwable) => messagePromise failure throwable
    }
    messagePromise.future
  }

  def getFlows(): Future[List[Flow]] = {
    val flowPromise = promise[List[Flow]]
    getRequest(baseUrl + "/flows") onComplete {
      case Success(response) => {
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
      case Failure(throwable) => flowPromise failure throwable
    }
    flowPromise.future
  }

  private def getRequest(resource: String): Future [String]= {
    Log.v("debug", "Getting: " + resource)
    val getPromise = promise[String]
    client.get(resource, new AsyncHttpResponseHandler() {
    	override def onSuccess(response: String) = {
    	  Log.v("debug", "Got response: " + response)
    	  getPromise success response
    	}
    	override def onFailure(throwable: Throwable, error: String) = {
    	  Log.v("debug", "Error: " + error)
    	  getPromise failure(throwable)
    	}
    })
    getPromise.future
  }

}