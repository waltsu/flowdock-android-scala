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
import scala.concurrent._
import com.waltsu.flowdock.models.Flow
import android.content.Intent
import android.view.MenuItem

class MainActivity extends Activity {
	var menuProgress: Option[MenuItem] = None

	override def onCreate(savedInstaneState: Bundle): Unit = {
	  super.onCreate(savedInstaneState)
	  setContentView(R.layout.activity_main)
	  flowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    override def onItemClick(adpterView: AdapterView[_], view: View, pos: Int, id: Long) = {
	      val selectedFlow: Flow = flowList.getAdapter().getItem(pos).asInstanceOf[Flow]
	      val flowIntent: Intent = new Intent(MainActivity.this, classOf[FlowActivity])
	      flowIntent.putExtra("flowUrl", selectedFlow.apiUrl)
	      flowIntent.putExtra("flowName", selectedFlow.name)
	      startActivity(flowIntent)
	    }
	  })
	    
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
	
	override def onResume(): Unit = {
	  super.onResume();
	  val flowPromise = FlowdockApi.getFlows()
	  val usersPromise = FlowdockApi.getUsers()
	  
	  flowPromise onSuccess {
	    case flows =>
          utils.runOnUiThread(this, () => flowList.setAdapter(flowListAdapter(flows)))
	  }
	  usersPromise onSuccess {
	    case users => 
	      ApplicationState.currentUsers = users
	  } 
	  
	  for {
	    flowDone <- flowPromise
	    usersDone <- usersPromise
	  } yield toggleLoading(false)
	}
	
	def flowList: ListView = findViewById(R.id.flowList).asInstanceOf[ListView]
	def toggleLoading(visible: Boolean) = {
	  menuProgress match {
	   case Some(item) => utils.runOnUiThread(MainActivity.this, () => item.setVisible(visible))
	   case None => Log.v("debug", "No menu item available")
	  }
	}
	
	def flowListAdapter(flows: List[Flow]): ArrayAdapter[Flow] = {
	  new FlowAdapter(getApplicationContext(), flows)
	}
}