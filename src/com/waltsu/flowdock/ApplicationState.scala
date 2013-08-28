package com.waltsu.flowdock

import com.waltsu.flowdock.models.User

/*
 * Stores information of the application.
 * It might be better to get rid of this and handle
 * situations with more functional-style
 */
object ApplicationState {
  var currentUsers: List[User] = List()
  val apiToken = "change"
  
}