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
import com.handmark.pulltorefresh.library.PullToRefreshListView
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener
import com.handmark.pulltorefresh.library.PullToRefreshBase

class FlowActivity extends Activity {
    var messages = List[FlowMessage]()
    var menuProgress: MenuItem = null
    var menuSendMessage: MenuItem = null
    var receiveMessages = true

    def replaceMessageModels(newMessages: List[FlowMessage]) = {
      messages = newMessages.filter((flowMessage) => flowMessage.canBeShown)
    }
    def addFirstToMessageModels(newMessages: List[FlowMessage]) = {
      replaceMessageModels(newMessages ::: messages)  
    }
	def addLastToMessageModels(newMessages: List[FlowMessage]) = {
	  replaceMessageModels(messages ::: newMessages)
	}


	def flowUrl =
	  getIntent().getExtras().getString("flowUrl")
	def streamUrl =
	  // Ugly :(
	  flowUrl.replace("https://api", "https://stream")
	def flowName =
	  getIntent().getExtras().getString("flowName")
	  
	def messagePullToRefreshListView: SmartScrollPullToRefreshListView = findViewById(R.id.flowMessageList).asInstanceOf[SmartScrollPullToRefreshListView]
	def messageList: ListView = messagePullToRefreshListView.getRefreshableView()
	def inputEditText: EditText = findViewById(R.id.flowInputText).asInstanceOf[EditText]

	override def onCreate(savedInstance: Bundle): Unit = {
	  super.onCreate(savedInstance)
	  setContentView(R.layout.activity_flow)
	  
	  setTitle(flowName)
	  getActionBar().setDisplayHomeAsUpEnabled(true)
	  
	  inputEditText.addTextChangedListener(new TextWatcher() {
	    override def afterTextChanged(et: Editable) = {
	      if (menuSendMessage != null)
	      	menuSendMessage.setVisible(et.length() > 0)
	    }
	    override def beforeTextChanged(s: CharSequence, st: Int, c: Int, after: Int) = {}
	    override def onTextChanged(s: CharSequence, st: Int, b: Int, c: Int) = {}
	  })

	  messagePullToRefreshListView.setOnRefreshListener(new OnRefreshListener[ListView]() {
	    override def onRefresh(list: PullToRefreshBase[ListView]) = {
	      val untilMessage: FlowMessage = messages.head
	      FlowdockApi.getMessagesUntil(FlowActivity.this, flowUrl, untilMessage, receiveNewMessages(false, true))
	    }
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
	    case android.R.id.home =>
	      finish()
	      true
	    case _ => false
	  }
	}
	
	override def onResume: Unit = {
	  super.onResume()
	  if (ApplicationState.currentUsers.isEmpty) return finish()
	  toggleLoading(true)
	  FlowdockApi.getLatestMessages(this, flowUrl, receiveNewMessages(true, false, true))

      Log.v("debug", "Starting to consume messages from stream")
      val streamClient = new FlowdockStreamClient(this, streamUrl)
	  streamClient.streamingMessages(receiveNewMessage)
	  streamClient.errorCallback = (message: String) =>
	    toggleLoading(true)
	  streamClient.successCallback = (message: String) =>
	    toggleLoading(false)

	  receiveMessages = true
	}
	
	override def onPause = {
	  super.onPause()
	  receiveMessages = false
	}
	
	def receiveNewMessage(message: FlowMessage) = {
	    if (message.canBeShown) {
		  addLastToMessageModels(List(message))
		  updateMessageList()
		}
	    receiveMessages 
	}
	
	def receiveNewMessages(replace: Boolean, first: Boolean = true, scrollDown: Boolean = false)(messages: Option[List[FlowMessage]]) = {
	  messages match {
        case Some(newMessages) => {
          if (replace)
            replaceMessageModels(newMessages)
          else
            if (first)
	          addFirstToMessageModels(newMessages)
	        else
	          addLastToMessageModels(newMessages)
		  updateMessageList()
		  if (scrollDown)
		    messagePullToRefreshListView.scrollToBottom
		}
	    case None => {
	      Toast.makeText(FlowActivity.this, "Problem when fetching messages", Toast.LENGTH_LONG).show()
	    }
	  }
	  toggleLoading(false)
	  messagePullToRefreshListView.onRefreshComplete()
	}
	
	def updateMessageList() = {
      utils.runOnUiThread(this, () => {
        // If we always create new adapter, listview's position will reset to 0
        // TODO: Figure how to use existing adapter without mutable states :)
        // We might need to switch messages to mutable list and use notifyDataSetChange on adapter... :(
        messagePullToRefreshListView.savePosition()
        messageList.setAdapter(new FlowMessageAdapter(getApplicationContext(), messages))
        messagePullToRefreshListView.restorePosition()
      })
	}
        
	def toggleLoading(visible: Boolean) = {
	  if (menuProgress != null)
	    utils.runOnUiThread(FlowActivity.this, () => menuProgress.setVisible(visible))
	}
}