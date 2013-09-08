package com.waltsu.flowdock

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.future

import org.apache.http.HttpResponse
import org.apache.http.auth.AuthScope
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject

import com.waltsu.flowdock.models.FlowMessage
import com.waltsu.flowdock.models.ModelBuilders

import android.content.Context
import android.os.Handler
import android.util.Log
import android.widget.Toast

class FlowdockStreamClient(val context: Context, val flowUrl: String) {
  val maxCooldownTime = 60
  val handler = new Handler()
  var errorCallback: (String => Unit) = null
  var successCallback: (String) => Unit = null

  def streamingMessages(cb: (FlowMessage) => Boolean, cd: Int = 1): Unit = future[Unit] {
    Log.v("debug", "Streaming from: " + flowUrl)
    var coolDown = cd
    try {
	  val streamClient: DefaultHttpClient = new DefaultHttpClient()
	  val basicAuth: Credentials = new UsernamePasswordCredentials(ApplicationState.apiToken(context), "")
	  streamClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), basicAuth)
	  val req: HttpGet = new HttpGet()

	  req.setHeader("Accept", "application/json")
	  req.setHeader("Content-Type", "application/json")
	  req.setHeader("Connection", "Keep-Alive")
	  req.setURI(new URI(flowUrl))
	
	  val response: HttpResponse = streamClient.execute(req)
	  val inStream: InputStream = response.getEntity().getContent()
	  val reader: BufferedReader = new BufferedReader(new InputStreamReader(inStream))
	    
	  def consumeLine(input: BufferedReader): Unit = {
	    val line = input.readLine()
	    if (successCallback != null) successCallback(line)
	    coolDown = 1
		if (line.startsWith("{")) {
		  val rawMessage = utils.JSONObjectToMap(new JSONObject(line))
		  val flowMessage = ModelBuilders.constructFlowMessage(rawMessage)
		  val more = cb(flowMessage)
		  if (!more) {
		    Log.v("debug", "Closing the stream")
		    inStream.close()
		  } else
		    consumeLine(input)
		} else {
	     consumeLine(input)
		}
	  }
	  consumeLine(reader)
	} catch {
	  case ioe: IOException =>
	    Log.v("debug", "Got io exception: " + ioe.getMessage().toString())
	    if (errorCallback != null) errorCallback(ioe.getMessage().toString())
	    if (coolDown < maxCooldownTime) {
	      Log.v("debug", "Posting new event to handler because cooldown time is: " + coolDown)
	      handler.postDelayed(new Runnable() {
	        override def run() {
		      Log.v("debug", "Maximum time not exceed, opening new connection")
		      streamingMessages(cb, coolDown * 2)
	        }
	      }, coolDown * 1000)
	    } else {
	      handler.post(new Runnable() {
	        override def run() {
		      Toast.makeText(context, "Cannot stream messages", Toast.LENGTH_LONG).show()
	        } 
	      })
	    }
	}
  }
    
}