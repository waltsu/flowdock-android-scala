package com.waltsu.flowdock

import com.loopj.android.http.RequestParams
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler

class SimpleResponseHandler(cb: (Option[String]) => Unit) extends AsyncHttpResponseHandler {
  override def onSuccess(response: String) = {
    Log.v("debug", "Got response: " + response)
    cb(Some(response))
  }
  override def onFailure(throwable: Throwable, error: String) = {
    Log.v("debug", "Throwable: " + throwable.toString())
    Log.v("debug", "Error: " + error)
    cb(None)
 }
}

object RESTClient {
    val client: AsyncHttpClient = new AsyncHttpClient()

    client.setBasicAuth(ApplicationState.apiToken, "")
    client.addHeader("Accept", "application/json")
    client.addHeader("Content-Type", "application/json")
    
    def postRequest(resource: String, params: Map[String, String], cb: (Option[String]) => Unit) = {
    Log.v("debug", "Posting: " + resource)
    val requestParams = params match {
      case null => new RequestParams()
      case _ => params.foldLeft[RequestParams](new RequestParams())({ case (acc, (k, v)) => {
          acc.put(k, v)
          acc
        }
      })
    }
    Log.v("debug", "RequestParams: " + requestParams.toString())
    client.post(resource, requestParams, new SimpleResponseHandler(cb))
  }
  def getRequest(resource: String, cb: (Option[String]) => Unit) = {
    Log.v("debug", "Getting: " + resource)
    client.get(resource, new SimpleResponseHandler(cb))
  }


}