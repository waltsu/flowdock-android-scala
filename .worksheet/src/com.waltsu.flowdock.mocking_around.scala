package com.waltsu.flowdock

import com.waltsu.flowdock.FlowdockApi

import collection.JavaConversions._

object mocking_around {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(248); 
  val string = "[{ \"obj1\": \"asdf\", \"obj2\": \"fdsa\"}, { \"obj1\": [{ \"key\": \"asdf\" }], \"obj2\": \"fdsa\"}]";System.out.println("""string  : String = """ + $show(string ));$skip(26); val res$0 = 
  utils.parseJSON(string);System.out.println("""res0: Product = """ + $show(res$0))}
}
