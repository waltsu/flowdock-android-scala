package com.waltsu.flowdock

import com.waltsu.flowdock.FlowdockApi

object mocking_around {
  FlowdockApi.getUrl("http://google.fi")          //> java.lang.ExceptionInInitializerError
                                                  //| 	at com.waltsu.flowdock.mocking_around$$anonfun$main$1.apply$mcV$sp(com.w
                                                  //| altsu.flowdock.mocking_around.scala:6)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                  //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                  //| orksheetSupport.scala:65)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                  //| ksheetSupport.scala:75)
                                                  //| 	at com.waltsu.flowdock.mocking_around$.main(com.waltsu.flowdock.mocking_
                                                  //| around.scala:5)
                                                  //| 	at com.waltsu.flowdock.mocking_around.main(com.waltsu.flowdock.mocking_a
                                                  //| round.scala)
                                                  //| Caused by: java.lang.RuntimeException: Stub!
                                                  //| 	at org.apache.http.params.AbstractHttpParams.<init>(AbstractHttpParams.j
                                                  //| ava:5)
                                                  //| 	at org.apache.http.params.BasicHttpParams.<init>(BasicHttpParams.java:6)
                                                  //| 
                                                  //| 	at com.loopj.android.http.AsyncHttpClient.<init>(AsyncHttpClient.java:11
}