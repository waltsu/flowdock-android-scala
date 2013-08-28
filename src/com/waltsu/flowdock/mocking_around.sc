package com.waltsu.flowdock

import collection.JavaConversions._
import org.json.JSONObject
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object mocking_around {
  def getU = future {
    "val"
  }                                               //> getU: => scala.concurrent.Future[String]

  Await.result(getU, 0 nanos)                     //> res0: String = val
}