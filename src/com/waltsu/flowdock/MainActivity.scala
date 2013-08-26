package com.waltsu.flowdock

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.widget.TextView
import android.util.Log
import android.widget.ListView
import android.widget.ArrayAdapter
import utils._
import android.widget.AdapterView
import android.view.View
import android.widget.Adapter

import scala.concurrent.ExecutionContext.Implicits.global

class MainActivity extends Activity {
	override def onCreate(savedInstaneState: Bundle): Unit = {
	  super.onCreate(savedInstaneState)
	  setContentView(R.layout.activity_main)
	  flowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    override def onItemClick(adpterView: AdapterView[_], view: View, pos: Int, id: Long) = {
	      Log.v("debug", pos.toString)
	    }
	  })
	    
	}
	
	override def onCreateOptionsMenu(menu: Menu): Boolean = {
	  getMenuInflater().inflate(R.menu.main, menu)
	  true
	}
	
	override def onResume(): Unit = {
	  super.onResume();
	  
	  FlowdockApi.getFlows() onSuccess {
	    case flows => {
	      flows match {
	        case Some(x) => {
	          val adapter = flowListAdapter(x.asInstanceOf[List[Map[String, Any]]])
	          utils.runOnUiThread(this, () => flowList.setAdapter(adapter))
	        }
	        case None =>
	          Log.v("debug", "No flows")
	      }
	    }
	  }
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
	  new ArrayAdapter(getApplicationContext(), R.layout.basic_list_item, toJavaList[String](names))
	}
}