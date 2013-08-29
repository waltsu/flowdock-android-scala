package com.waltsu.flowdock

import com.waltsu.flowdock.models.FlowMessage
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.future
import org.apache.http.HttpResponse
import com.waltsu.flowdock.models.ModelBuilders
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import java.io.BufferedReader
import java.io.InputStream
import android.util.Log
import org.json.JSONObject
import org.apache.http.auth.AuthScope
import java.io.InputStreamReader
import java.net.URI
import org.apache.http.auth.Credentials
import java.io.IOException

object FlowdockStreamClient {
  /*
   * Currently when encountering io-error tries to connect again. Some throttling/smarter reconnect logic needed!
   * This will drain the battery pretty fast
   */
  def streamingMessages(flowUrl: String, cb: (FlowMessage) => Boolean): Unit = future[Unit] {
    Log.v("debug", "Streaming from: " + flowUrl)
    try {
	  val streamClient: DefaultHttpClient = new DefaultHttpClient()
	  val basicAuth: Credentials = new UsernamePasswordCredentials(ApplicationState.apiToken, "")
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
		if (line.startsWith("{")) {
		  val rawMessage = utils.JSONObjectToMap(new JSONObject(line))
		  val flowMessage = ModelBuilders.constructFlowMessage(rawMessage)
		  val more = cb(flowMessage)
		  if (!more)
		    inStream.close()
		  else
		    consumeLine(input)
		} else {
	     consumeLine(input)
		}
	  }
	  consumeLine(reader)
	} catch {
	  case ioe: IOException =>
	    Log.v("debug", "Got io exception: " + ioe.getMessage().toString())
	    
	    Log.v("debug", "Opening new connection")
	    streamingMessages(flowUrl, cb)
	}
  }
    
}