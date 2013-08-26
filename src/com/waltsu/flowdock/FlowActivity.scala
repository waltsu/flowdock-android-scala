package com.waltsu.flowdock

import android.app.Activity
import android.os.Bundle

class FlowActivity extends Activity {
	override def onCreate(savedInstance: Bundle): Unit = {
	  super.onCreate(savedInstance)
	  setContentView(R.layout.activity_flow)
	}
}