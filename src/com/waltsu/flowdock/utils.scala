package com.waltsu.flowdock

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object utils {
  def toJavaList[T](list: List[T]): java.util.List[T] =
    ListBuffer(list: _*)
}