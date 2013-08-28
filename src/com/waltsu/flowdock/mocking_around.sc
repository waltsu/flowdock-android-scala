package com.waltsu.flowdock

import collection.JavaConversions._
import org.json.JSONObject
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object mocking_around {
  val s = for {
    i <- 1 to 5
    j <- 6 to 10
  } yield(i, j)                                   //> s  : scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((1,6), (1,7)
                                                  //| , (1,8), (1,9), (1,10), (2,6), (2,7), (2,8), (2,9), (2,10), (3,6), (3,7), (3
                                                  //| ,8), (3,9), (3,10), (4,6), (4,7), (4,8), (4,9), (4,10), (5,6), (5,7), (5,8),
                                                  //|  (5,9), (5,10))
}