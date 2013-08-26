package com.waltsu.flowdock

import com.waltsu.flowdock.FlowdockApi

import collection.JavaConversions._

object mocking_around {
  val string = "[{ \"obj1\": \"asdf\", \"obj2\": \"fdsa\"}, { \"obj1\": [{ \"key\": \"asdf\" }], \"obj2\": \"fdsa\"}]"
                                                  //> string  : String = [{ "obj1": "asdf", "obj2": "fdsa"}, { "obj1": [{ "key": "
                                                  //| asdf" }], "obj2": "fdsa"}]
  utils.parseJSON(string)                         //> res0: Product = List(Map(obj1 -> asdf, obj2 -> fdsa), Map(obj1 -> List(Map(k
                                                  //| ey -> asdf)), obj2 -> fdsa))
}