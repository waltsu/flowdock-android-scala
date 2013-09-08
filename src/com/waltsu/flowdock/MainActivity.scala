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
import android.app.AlertDialog
import android.widget.EditText
import android.content.DialogInterface

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
	  getMenuInflater().inflate(R.menu.main_menu, menu)
	  val menuItem = menu.findItem(R.id.menuItemProgress).asInstanceOf[MenuItem]
	  menuProgress = menuItem match {
	    case null => None
	    case m => Some(m)
	  }
	  true
	}
	
	override def onOptionsItemSelected(item: MenuItem): Boolean = {
	  item.getItemId() match {
	    case R.id.menuInputAPIKey => 
	      openAPIKeyModal
	      true
	    case R.id.menuInputAPIUrl =>
	      openAPIUrlModal
	      true
	    case _ => false
	  }
	}
	
	override def onResume(): Unit = {
	  super.onResume();
	  if (ApplicationState.isApiTokenSet(this))
	    fetchServerData
      else
        openAPIKeyModal
	}
	
	def flowList: ListView = findViewById(R.id.flowList).asInstanceOf[ListView]
	
	def flowListAdapter(flows: List[Flow]): ArrayAdapter[Flow] = {
	  new FlowAdapter(getApplicationContext(), flows)
	}
	
	def fetchServerData = {
	  toggleLoading(true)

	  val flowPromise = FlowdockApi.getFlows(this)
	  val usersPromise = FlowdockApi.getUsers(this)
	  
	  flowPromise onSuccess {
	    case flows =>
          utils.runOnUiThread(this, () => flowList.setAdapter(flowListAdapter(flows)))
	  }
	  usersPromise onSuccess {
	    case users => 
	      ApplicationState.currentUsers = users
	  } 
	  flowPromise onFailure { case f => toggleLoading(false) }
	  usersPromise onFailure { case f => toggleLoading(false) }
	  
	  for {
	    flowDone <- flowPromise
	    usersDone <- usersPromise
	  } yield toggleLoading(false)
	  
	}
	def openAPIKeyModal = {
	  val input = new EditText(this)
	  input.setText(ApplicationState.apiToken(this))
	  new AlertDialog.Builder(this)
	    .setTitle("API-key")
	    .setMessage("Please set API-key")
	    .setView(input)
	    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	      override def onClick(dialog: DialogInterface, button: Int) {
	        ApplicationState.setApiToken(MainActivity.this, input.getText().toString())
	        Log.v("debug", "Api token set: " + input.getText().toString)
	        fetchServerData
	      } 
	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	      override def onClick(dialog: DialogInterface, button: Int) {}
	    })
	    .show()
	}
	def openAPIUrlModal = {
	  val input = new EditText(this)
	  input.setText(ApplicationState.apiUrl(this))
	  new AlertDialog.Builder(this)
	    .setTitle("API-url")
	    .setMessage("Please set API-url (Don't change if you don't know what you are doing)")
	    .setView(input)
	    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	      override def onClick(dialog: DialogInterface, button: Int) {
	        ApplicationState.setApiUrl(MainActivity.this, input.getText().toString())
	        Log.v("debug", "Api url set: " + input.getText().toString)
	        fetchServerData
	      } 
	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	      override def onClick(dialog: DialogInterface, button: Int) {}
	    })
	    .show()
	}

	def toggleLoading(visible: Boolean) = {
	  menuProgress match {
	   case Some(item) => utils.runOnUiThread(MainActivity.this, () => item.setVisible(visible))
	   case None => Log.v("debug", "No menu item available")
	  }
	}
}