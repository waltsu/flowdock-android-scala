package com.waltsu.flowdock

import com.loopj.android.http._
import android.util.Log
import scala.util.parsing.json.JSON
import scala.concurrent.{ Future, future, promise }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

// TODO: Some sort of cache
object FlowdockApi {
  val client: AsyncHttpClient = new AsyncHttpClient()
  val apiToken = "4e38bb9f024539cea526e87262999d35"
    
  client.setBasicAuth(apiToken, "")
  client.addHeader("Accept", "application/json")
  client.addHeader("Content-Type", "application/json")
    
  val baseUrl = "https://api.flowdock.com"
    
  def getFlows(): Future[Option[Any]] = {
    val flowPromise = promise[Option[Any]]
    getRequest("/flows") onComplete {
      case Success(response) =>  flowPromise success(JSON.parseFull(response))
      case Failure(throwable) => flowPromise failure throwable
    }
    flowPromise.future
  }
    

  private def getRequest(resource: String): Future [String]= {
    Log.v("debug", "Getting: " + baseUrl + resource)
    val getPromise = promise[String]
    client.get(baseUrl + resource, new AsyncHttpResponseHandler() {
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