package com.waltsu.flowdock

import android.app.Activity
import android.os.Bundle
import android.util.Log

class FlowActivity extends Activity {
	override def onCreate(savedInstance: Bundle): Unit = {
	  super.onCreate(savedInstance)
	  setContentView(R.layout.activity_flow)
	  
	  Log.v("debug", apiUrl)
	}
	
	def apiUrl =
	  getIntent().getExtras().getString("flowUrl")
}