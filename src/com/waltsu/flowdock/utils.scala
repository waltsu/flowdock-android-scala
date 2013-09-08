package com.waltsu.flowdock

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import android.app.Activity
import collection.mutable.Map
import collection.immutable.{ Map => ImmutableMap }
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
      val keyString = k.toString()
      map(keyString) = json.get(keyString) 
      map
    }).toMap
      
  }
  
  def getStringOrEmpty(m: ImmutableMap[String, Any], key: String): String = {
    m.get(key) match {
      case Some(x) => x.toString
      case None => ""
    }
  }
  
  def getMapFromOptionJSON(option: Option[Any]): ImmutableMap[String, Any] = {
    option match {
      case Some(x) => {
        x match {
          case json: JSONObject => JSONObjectToMap(json)
          case _ => ImmutableMap[String, Any]()
        }
      }
      case None => ImmutableMap[String, Any]()
    } 
  }
  def getListFromOptionJSON(option: Option[Any]) = {
   option match {
     case Some(x) => {
       x match {
         case json: JSONArray => JSONArrayToList(json)
         case _ => List[Any]()
       }
     }
     case None => List[Any]()
   } 
    
  }
	  
}