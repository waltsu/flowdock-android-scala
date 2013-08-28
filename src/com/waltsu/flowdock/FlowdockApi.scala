package com.waltsu.flowdock

import com.loopj.android.http._
import android.util.Log
import scala.util.parsing.json.JSON
import scala.concurrent.{ Future, future, promise }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }
import com.waltsu.flowdock.models.Flow
import com.waltsu.flowdock.models.Flow
import com.waltsu.flowdock.models.FlowMessage
import org.json.JSONArray
import org.json.JSONObject
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.auth.AuthScope
import org.apache.http.client.methods.HttpGet
import java.net.URI
import org.apache.http.HttpResponse
import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader

// TODO: Some sort of cache
object FlowdockApi {
  val client: AsyncHttpClient = new AsyncHttpClient()
  //val apiToken = "change"
  val apiToken = "bf8c52b76b17f275d4a9e37189847ae6"
    
  client.setBasicAuth(apiToken, "")
  client.addHeader("Accept", "application/json")
  client.addHeader("Content-Type", "application/json")
  
  val persistentClient: AsyncHttpClient = new AsyncHttpClient()
  persistentClient.setBasicAuth(apiToken, "")
  persistentClient.addHeader("Accept", "application/json")
  persistentClient.addHeader("Content-Type", "application/json")
  persistentClient.addHeader("Connection", "Keep-Alive")
    
  val baseUrl = "https://api.flowdock.com"
    
  def streamingMessages(flowUrl: String, cb: (FlowMessage) => Boolean): Unit = future[Unit] {
    Log.v("debug", "Streaming from: " + flowUrl)
    val streamClient: DefaultHttpClient = new DefaultHttpClient()
    val basicAuth: Credentials = new UsernamePasswordCredentials(apiToken, "")
    streamClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), basicAuth)
    val req: HttpGet = new HttpGet()

    req.setHeader("Accept", "application/json")
    req.setHeader("Content-Type", "application/json")
    req.setHeader("Connection", "Keep-Alive")
    req.setURI(new URI(flowUrl))

    val response: HttpResponse = streamClient.execute(req)
    val inStream: InputStream = response.getEntity().getContent()
    val reader: BufferedReader = new BufferedReader(new InputStreamReader(inStream))
    
    // More functional approach needed
    var running: Boolean = true
    do {
      val line = reader.readLine()  
      Log.v("debug", "Got line: " + line)
      if (line.startsWith("{")) {
	    val rawMessage = utils.JSONObjectToMap(new JSONObject(line))
	    val event = rawMessage.get("event")
	    val sent = rawMessage.get("sent")
	    val content = rawMessage.get("content")
	    val flowMessage = new FlowMessage(event.get.toString, sent.get.asInstanceOf[Long], content.get.toString)
		running = cb(flowMessage)
      }
    } while (running)
    Log.v("debug", "Stopped for listening stream")
      
    inStream.close()
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
          val event = m.get("event")
          val sent = m.get("sent")
          val content = m.get("content")
          new FlowMessage(event.get.toString, sent.get.asInstanceOf[Long], content.get.toString)
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
          val name = f.get("name")
          val apiUrl = f.get("url")
          new Flow(name.get.toString, apiUrl.get.toString)
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