package com.waltsu.flowdock

import com.loopj.android.http._
import android.util.Log
import scala.util.parsing.json.JSON

object FlowdockApi {
  val client: AsyncHttpClient = new AsyncHttpClient()
  val apiToken = "4e38bb9f024539cea526e87262999d35"
    
  client.setBasicAuth(apiToken, "")
  client.addHeader("Accept", "application/json")
  client.addHeader("Content-Type", "application/json")
    
  val baseUrl = "https://api.flowdock.com"
    
  def getFlows(cb: Option[Any] => Unit) = {
    getRequest("/flows", response =>
      cb(JSON.parseFull(response))
    )
  }
    

  private def getRequest(resource: String, callback: String => Unit) = {
    Log.v("debug", "Getting: " + baseUrl + resource)
    client.get(baseUrl + resource, new AsyncHttpResponseHandler() {
    	override def onSuccess(response: String) = {
    	  Log.v("debug", "Got response: " + response)
    	  callback(response)
    	}
    	override def onFailure(throwable: Throwable, error: String) = {
    	  Log.v("debug", "Error: " + error)
    	}
    })
  }

}