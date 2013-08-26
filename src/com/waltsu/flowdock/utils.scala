package com.waltsu.flowdock

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import android.app.Activity

object utils {
  def toJavaList[T](list: List[T]): java.util.List[T] =
    ListBuffer(list: _*)
    
   def runOnUiThread(activity: Activity, func: () => Unit): Unit =
     activity.runOnUiThread(new Runnable() {
    	override def run: Unit = 
    	  func()
    	  
	})

     
}