package com.waltsu.flowdock

import com.waltsu.flowdock.models.User
import android.content.Context
import android.content.SharedPreferences

/*
 * Stores information of the application.
 * It might be better to get rid of this and handle
 * situations with more functional-style
 */
object ApplicationState {
  var currentUsers: List[User] = List()
    
  def isApiTokenSet(c: Context) = apiToken(c).length() > 0
  def setApiToken(c: Context, token: String) = {
    c.getSharedPreferences("prefs", 0).edit().putString("apiToken", token).commit()
  }
  def apiToken(c: Context) = {
    c.getSharedPreferences("prefs", 0).getString("apiToken", "")
  }
  
  def getApiUrl(c: Context) = {
    c.getSharedPreferences("prefs", 0).getString("apiUrl", "https://api.flowdock.com")
  }
}