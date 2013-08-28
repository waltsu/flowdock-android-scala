package com.waltsu.flowdock

import android.app.Activity
import scala.concurrent.ExecutionContext.Implicits.global
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.waltsu.flowdock.models.FlowMessage

class FlowActivity extends Activity {
    var messages = List[FlowMessage]()

	override def onCreate(savedInstance: Bundle): Unit = {
	  super.onCreate(savedInstance)
	  setContentView(R.layout.activity_flow)
	  
	  setTitle("Change to flow name")
	}
	
	override def onResume: Unit = {
	  super.onResume()
	  
	  FlowdockApi.getMessages(flowUrl) onSuccess {
	    case newMessages =>
	      messages = newMessages
	      updateMessageList()
	      scrollListToBottom(messageList)
	  }
	  Log.v("debug", "Starting to consume messages from stream")
	  FlowdockApi.streamingMessages(streamUrl, (message: FlowMessage) => {
	    if (!message.event.startsWith("activity"))
		  addToMessageList(message)
	    true 
	  })
	}
	
	def messageList: ListView = findViewById(R.id.flowMessageList).asInstanceOf[ListView]
	def updateMessageList() =
      utils.runOnUiThread(this, () => messageList.setAdapter(new FlowMessageAdapter(getApplicationContext(), messages)))
     def scrollListToBottom(list: ListView) =
       utils.runOnUiThread(this, () => list.setSelection(list.getAdapter().getCount() - 1))

	def flowUrl =
	  getIntent().getExtras().getString("flowUrl")
	def streamUrl =
	  // Ugly :(
	  flowUrl.replace("https://api", "https://stream")
	  
	def addToMessageList(message: FlowMessage) = {
	  messages = messages ::: List(message)
	  updateMessageList()
	  scrollListToBottom(messageList)
	}
}