package com.waltsu.flowdock

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.widget.TextView
import android.util.Log
import android.widget.ListView
import android.widget.ArrayAdapter
import utils._

class MainActivity extends Activity {
	override def onCreate(savedInstaneState: Bundle): Unit = {
	  super.onCreate(savedInstaneState)
	  setContentView(R.layout.activity_main)
	}
	
	override def onCreateOptionsMenu(menu: Menu): Boolean = {
	  getMenuInflater().inflate(R.menu.main, menu)
	  true
	}
	
	override def onResume(): Unit = {
	  super.onResume();
	  
	  FlowdockApi.getFlows(flows =>
	    flows match {
	      case Some(x) => {
	        val adapter = flowListAdapter(x.asInstanceOf[List[Map[String, Any]]])
	        flowList.setAdapter(adapter)
	      }
	      case None =>
	        Log.v("debug", "No flows")
	    }
	  )
	}
	
	def flowList: ListView = findViewById(R.id.flowList).asInstanceOf[ListView]
	
	def flowListAdapter(flows: List[Map[String, Any]]): ArrayAdapter[String] = {
	  val names = flows.map((m: Map[String, Any]) => {
	    val name = m.get("name")
	    name match {
	      case Some(x) => x.toString
	      case None => ""
	    }
	  })
	  new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, toJavaList[String](names))
	}
}