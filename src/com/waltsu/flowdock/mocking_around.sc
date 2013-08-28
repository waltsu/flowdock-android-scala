package com.waltsu.flowdock

import collection.JavaConversions._
import org.json.JSONObject

object mocking_around {
  println("yo")                                   //> yo
  var list = List(1,2,3)                          //> list  : List[Int] = List(1, 2, 3)
  list = list ::: List(4)
  list                                            //> res0: List[Int] = List(1, 2, 3, 4)
}