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
import com.waltsu.flowdock.models.Flow
import android.content.Intent

class MainActivity extends Activity {
	override def onCreate(savedInstaneState: Bundle): Unit = {
	  super.onCreate(savedInstaneState)
	  setContentView(R.layout.activity_main)
	  flowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    override def onItemClick(adpterView: AdapterView[_], view: View, pos: Int, id: Long) = {
	      val selectedFlow: Flow = flowList.getAdapter().getItem(pos).asInstanceOf[Flow]
	      val flowIntent: Intent = new Intent(MainActivity.this, classOf[FlowActivity])
	      flowIntent.putExtra("flowUrl", selectedFlow.apiUrl)
	      startActivity(flowIntent)
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
	    case flows =>
          utils.runOnUiThread(this, () => flowList.setAdapter(flowListAdapter(flows)))
	  }
	  FlowdockApi.getUsers onSuccess {
	    case users => ApplicationState.currentUsers = users
	  }
	}
	
	def flowList: ListView = findViewById(R.id.flowList).asInstanceOf[ListView]
	
	def flowListAdapter(flows: List[Flow]): ArrayAdapter[Flow] = {
	  new FlowAdapter(getApplicationContext(), flows)
	}
}