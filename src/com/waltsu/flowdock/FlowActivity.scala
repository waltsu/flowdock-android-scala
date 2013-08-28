package com.waltsu.flowdock

import android.app.Activity
import scala.concurrent.ExecutionContext.Implicits.global
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.waltsu.flowdock.models.FlowMessage
import android.view.Menu
import android.view.MenuItem

class FlowActivity extends Activity {
    var messages = List[FlowMessage]()
    var menuProgress: Option[MenuItem] = None

    def replaceMessageModels(newMessages: List[FlowMessage]) = {
      messages = newMessages.filter((flowMessage) => flowMessage.canBeShown)
    }

	def flowUrl =
	  getIntent().getExtras().getString("flowUrl")
	def streamUrl =
	  // Ugly :(
	  flowUrl.replace("https://api", "https://stream")
	def flowName =
	  getIntent().getExtras().getString("flowName")
	  

	override def onCreate(savedInstance: Bundle): Unit = {
	  super.onCreate(savedInstance)
	  setContentView(R.layout.activity_flow)
	  
	  setTitle(flowName)
	}
	
	override def onCreateOptionsMenu(menu: Menu): Boolean = {
	  getMenuInflater().inflate(R.menu.loading_menu, menu)
	  val menuItem = menu.findItem(R.id.menuItemProgress).asInstanceOf[MenuItem]
	  menuProgress = menuItem match {
	    case null => None
	    case m => Some(m)
	  }
	  true

	}
	
	override def onResume: Unit = {
	  super.onResume()
	  val messagesPromise = FlowdockApi.getMessages(flowUrl)
	  messagesPromise onSuccess {
	    case newMessages =>
	      replaceMessageModels(newMessages)
	      updateMessageList()
	      scrollMessageListToBottom()
	  }
	  
	  for {
	    messagesDone <- messagesPromise
	  } yield toggleLoading(false)

	  Log.v("debug", "Starting to consume messages from stream")
	  FlowdockStreamClient.streamingMessages(streamUrl, (message: FlowMessage) => {
	    if (!message.event.startsWith("activity"))
		  addToMessageList(message)
	    true 
	  })
	}
	
	def messageList: ListView = findViewById(R.id.flowMessageList).asInstanceOf[ListView]

	def updateMessageList() =
      utils.runOnUiThread(this, () => messageList.setAdapter(new FlowMessageAdapter(getApplicationContext(), messages)))

    def scrollMessageListToBottom() =
      utils.runOnUiThread(this, () => messageList.setSelection(messageList.getAdapter().getCount() - 1))

	def addToMessageList(message: FlowMessage) = {
	  replaceMessageModels(messages ::: List(message))
	  updateMessageList()
	  scrollMessageListToBottom()
	}

	def toggleLoading(visible: Boolean) = {
	  menuProgress match {
	   case Some(item) => utils.runOnUiThread(FlowActivity.this, () => item.setVisible(visible))
	   case None => Log.v("debug", "No menu item available")
	  }
	}
}