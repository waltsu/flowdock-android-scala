package com.waltsu.flowdock

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import android.app.Activity
import collection.mutable.Map
import collection.mutable.ListBuffer
import collection.JavaConversions._
import java.util.{ ArrayList, HashMap }
import org.json.JSONArray
import org.json.JSONObject

object utils {
  def toJavaList[T](list: List[T]): java.util.List[T] =
    ListBuffer(list: _*)
    
   def runOnUiThread(activity: Activity, func: () => Unit): Unit =
     activity.runOnUiThread(new Runnable() {
    	override def run: Unit = 
    	  func()
    	  
	})
	
  def JSONArrayToList(json: JSONArray) = {
    var list: List[Any] = Nil
    for (i <- 0 until json.length())
      list = list ::: List(json.get(i))
    list.toList
  }
  
  def JSONObjectToMap(json: JSONObject) = {
    var map: Map[String, Any] = Map[String, Any]()
    json.keys().foldLeft[Map[String, Any]](map)((acc, k: Any) => {
      val ks = k.toString()
      map(ks) = json.get(ks) 
      map
    }).toMap
      
  }
	  
}