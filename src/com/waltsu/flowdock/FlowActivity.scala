package com.waltsu.flowdock

import android.app.Activity
import scala.concurrent.ExecutionContext.Implicits.global
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.waltsu.flowdock.models.FlowMessage

class FlowActivity extends Activity {
	override def onCreate(savedInstance: Bundle): Unit = {
	  super.onCreate(savedInstance)
	  setContentView(R.layout.activity_flow)
	  
	  setTitle("Change to flow name")
	}
	
	def flowUrl =
	  getIntent().getExtras().getString("flowUrl")
	  
	override def onResume: Unit = {
	  super.onResume()
	  
	  FlowdockApi.getMessages(flowUrl) onSuccess {
	    case messages =>
	      //utils.runOnUiThread(this, () => messageList.setAdapter(new FlowMessageAdapter(getApplicationContext(), messages)))
	  }
	}
	
	def messageList: ListView = findViewById(R.id.flowMessageList).asInstanceOf[ListView]
}