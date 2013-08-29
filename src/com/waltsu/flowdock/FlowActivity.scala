package com.waltsu.flowdock

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._
import com.waltsu.flowdock.models.FlowMessage
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.EditText
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.text.TextWatcher
import android.text.Editable
import android.widget.Toast

class FlowActivity extends Activity {
    var messages = List[FlowMessage]()
    var menuProgress: MenuItem = null
    var menuSendMessage: MenuItem = null
    var receiveMessages = true

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
	  
	def messageList: ListView = findViewById(R.id.flowMessageList).asInstanceOf[ListView]
	def inputEditText: EditText = findViewById(R.id.flowInputText).asInstanceOf[EditText]

	override def onCreate(savedInstance: Bundle): Unit = {
	  super.onCreate(savedInstance)
	  setContentView(R.layout.activity_flow)
	  
	  setTitle(flowName)
	  
	  inputEditText.addTextChangedListener(new TextWatcher() {
	    override def afterTextChanged(et: Editable) = {
	      if (menuSendMessage != null)
	      	menuSendMessage.setVisible(et.length() > 0)
	    }
	    override def beforeTextChanged(s: CharSequence, st: Int, c: Int, after: Int) = {}
	    override def onTextChanged(s: CharSequence, st: Int, b: Int, c: Int) = {}
	  })
	}
	
	override def onCreateOptionsMenu(menu: Menu): Boolean = {
	  getMenuInflater().inflate(R.menu.flow_menu, menu)
	  menuProgress = menu.findItem(R.id.menuItemProgress).asInstanceOf[MenuItem]
	  menuSendMessage = menu.findItem(R.id.menuItemSendMessage).asInstanceOf[MenuItem]
	  true
	}
	
	override def onOptionsItemSelected(item: MenuItem): Boolean = {
	  item.getItemId() match {
	    case R.id.menuItemSendMessage =>
	      val text = inputEditText.getText().toString
	      val message = new FlowMessage("message", text)
	      inputEditText.setText("")
	      toggleLoading(true)
	      // Relying that message stream works so we don't need to update our list manually
	      FlowdockApi.sendMessage(this, flowUrl, message, (success: Boolean) => {
	        toggleLoading(false)
	        success match {
	          case true =>
	          case false => Toast.makeText(FlowActivity.this, "Failed to send message", Toast.LENGTH_LONG).show()
	        }  
	      })
	      true
	    case _ => false
	  }
	}
	
	override def onResume: Unit = {
	  super.onResume()
	  toggleLoading(true)
	  FlowdockApi.getMessages(this, flowUrl, receiveNewMessages)

      Log.v("debug", "Starting to consume messages from stream")
	  FlowdockStreamClient.streamingMessages(this, streamUrl, receiveNewMessage)
	  receiveMessages = true
	}
	
	override def onPause = {
	  super.onPause()
	  receiveMessages = false
	}
	
	def receiveNewMessage(message: FlowMessage) = {
	    if (!message.event.startsWith("activity"))
		  addToMessageList(message)
	    receiveMessages 
	}
	
	def receiveNewMessages(messages: Option[List[FlowMessage]]) = {
	  messages match {
        case Some(newMessages) => {
		  replaceMessageModels(newMessages)
		  updateMessageList()
		  scrollMessageListToBottom()
		  toggleLoading(false)
		}
	    case None => Log.v("debug", "No messages")
	  }
	}
	
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
	  if (menuProgress != null)
	    utils.runOnUiThread(FlowActivity.this, () => menuProgress.setVisible(visible))
	}
}