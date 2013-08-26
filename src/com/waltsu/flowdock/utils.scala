package com.waltsu.flowdock

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import android.app.Activity
import collection.mutable.Map
import com.json.parsers.{ JsonParserFactory, JSONParser }
import collection.JavaConversions._

import java.util.{ ArrayList, HashMap }
object utils {
  def toJavaList[T](list: List[T]): java.util.List[T] =
    ListBuffer(list: _*)
    
   def runOnUiThread(activity: Activity, func: () => Unit): Unit =
     activity.runOnUiThread(new Runnable() {
    	override def run: Unit = 
    	  func()
    	  
	})
	
  def parseJSON(string: String) = {
    val factory: JsonParserFactory = JsonParserFactory.getInstance()
    val parser: JSONParser = factory.newJsonParser()
    val parsedMap = parser.parseJson(string)
    parsedMap.toMap.get("root") match {
      case x if x.isInstanceOf[ArrayList[_]] => parseJSONArray(x.asInstanceOf[ArrayList[_]]) 
      case None => None
    }
  } 
  
  def parseJSONArray(list: ArrayList[_]): List[Any] = {
    list.toList.map((a: Any) => {
      a match {
        case x if x.isInstanceOf[HashMap[_, _]] => parseJSONObject(x.asInstanceOf[HashMap[_, _]])
        case x if x.isInstanceOf[ArrayList[_]] => parseJSONArray(x.asInstanceOf[ArrayList[_]])
      }
    })
  }
  
  def parseJSONObject(obj: HashMap[_, _]) = {
    val objMap = obj.toMap
    objMap.mapValues(c => {
      c match {
        case c if c.isInstanceOf[ArrayList[_]] => parseJSONArray(c.asInstanceOf[ArrayList[_]])
        case _ => c

      } 
    })
  }
}