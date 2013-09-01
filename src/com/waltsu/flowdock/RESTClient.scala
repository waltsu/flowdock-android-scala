package com.waltsu.flowdock

import com.loopj.android.http.RequestParams
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import android.content.Context
import org.apache.http.client.HttpResponseException
import android.widget.Toast

class SimpleResponseHandler(c: Context, cb: (Option[String]) => Unit) extends AsyncHttpResponseHandler {
  override def onSuccess(response: String) = {
    Log.v("debug", "Got response: " + response)
    cb(Some(response))
  }
  // TODO: Better error handling
  override def onFailure(throwable: Throwable, error: String) = {
    throwable match {
      case x if x.isInstanceOf[HttpResponseException] => {
        if (x.asInstanceOf[HttpResponseException].getStatusCode() == 401) {
         Toast.makeText(c, "Invalid API-key", Toast.LENGTH_LONG).show()
        }
      }
      case _ => Log.v("debug", "Other error!")
    }
    Log.v("debug", "Throwable: " + throwable.toString())
    Log.v("debug", "Error: " + error)
    cb(None)
 }
}
object RESTClient {
    val client: AsyncHttpClient = new AsyncHttpClient()
    client.addHeader("Accept", "application/json")
    client.addHeader("Content-Type", "application/json")
    
  def postRequest(c: Context, resource: String, params: Map[String, String], cb: (Option[String]) => Unit) = {
    client.setBasicAuth(ApplicationState.apiToken(c), "")
    Log.v("debug", "Posting: " + resource)
    val requestParams = mapToRequestParams(params)
    Log.v("debug", "RequestParams: " + requestParams.toString())
    client.post(resource, requestParams, new SimpleResponseHandler(c, cb))
  }
  def getRequest(c: Context, resource: String, cb: (Option[String]) => Unit): Unit = {
    getRequest(c, resource, null, cb)
  }
  
  def getRequest(c: Context, resource: String, params: Map[String, String], cb: (Option[String]) => Unit): Unit = {
    client.setBasicAuth(ApplicationState.apiToken(c), "")
    val requestParams = mapToRequestParams(params)
    Log.v("debug", "Getting: " + resource)
    client.get(resource, requestParams, new SimpleResponseHandler(c, cb))
    
  }

  private def mapToRequestParams(map: Map[String, String]): RequestParams = {
    map match {
      case null => new RequestParams()
      case _ => map.foldLeft[RequestParams](new RequestParams())({ case (acc, (k, v)) => {
          acc.put(k, v)
          acc
        }
      })
    } 
  }
}