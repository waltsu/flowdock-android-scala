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

// TODO: Some sort of cache
object FlowdockApi {
  val client: AsyncHttpClient = new AsyncHttpClient()
  val apiToken = "4e38bb9f024539cea526e87262999d35"
    
  client.setBasicAuth(apiToken, "")
  client.addHeader("Accept", "application/json")
  client.addHeader("Content-Type", "application/json")
    
  val baseUrl = "https://api.flowdock.com"
    
  def getMessages(flowUrl: String): Future[List[FlowMessage]] = {
    val messagePromise = promise[List[FlowMessage]]  
    getRequest(flowUrl + "/messages")  onComplete {
      case Success(response) => {
        val messages = JSON.parseFull(response)
        messages match {
          case Some(messageJson) =>
            val messageList = messageJson.asInstanceOf[List[Map[String, Any]]]
            val messageModels = messageList.map((m: Map[String, Any]) => {
              val event = m.get("event")
              val sent = m.get("sent")
              val content = m.get("content")
              new FlowMessage(event.get.toString, sent.get.asInstanceOf[Double], content.get.toString)
            })
            messagePromise success List[FlowMessage]()
          case None =>
            messagePromise failure new Exception("No messages")
        }
      }
      case Failure(throwable) => messagePromise failure throwable
    }
    messagePromise.future
  }

  def getFlows(): Future[List[Flow]] = {
    val flowPromise = promise[List[Flow]]
    getRequest(baseUrl + "/flows") onComplete {
      case Success(response) => {
        val flows = JSON.parseFull(response)
        flows match {
          case Some(flowJson) =>
            val flowList = flowJson.asInstanceOf[List[Map[String, Any]]]
            val flowModels = flowList.map((f: Map[String, Any]) => {
              val name = f.get("name")
              val apiUrl = f.get("url")
              new Flow(name.get.toString, apiUrl.get.toString)
	        })
            flowPromise success(flowModels)
          case None =>
            flowPromise failure new Exception("No flows") 
          
        }
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