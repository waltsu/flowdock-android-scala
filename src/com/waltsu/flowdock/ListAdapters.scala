package com.waltsu.flowdock

import com.waltsu.flowdock.models.Flow
import android.widget.ArrayAdapter
import android.content.Context
import utils._
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.waltsu.flowdock.models.FlowMessage

class FlowAdapter(c: Context, flows: List[Flow])
	extends ArrayAdapter[Flow](c, R.layout.basic_list_item, toJavaList[Flow](flows)) {
	
  override def getView(pos: Int, convertView: View, parent: ViewGroup): View = {
    val view = super.getView(pos, convertView, parent)   
    val textView = view.findViewById(R.id.basicText).asInstanceOf[TextView]
    textView.setText(flows(pos).name)
    view
  }
}

class FlowMessageAdapter(c: Context, messages: List[FlowMessage])
	extends ArrayAdapter[FlowMessage](c, R.layout.content_list_item, toJavaList[FlowMessage](messages)) {
  
  override def getView(pos: Int, convertView: View, parent: ViewGroup) = {
    messages(pos).getView(getContext())
  }
}