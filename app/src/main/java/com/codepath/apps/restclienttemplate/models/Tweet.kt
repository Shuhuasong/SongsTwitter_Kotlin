package com.codepath.apps.restclienttemplate.models

import android.util.Log
import com.codepath.apps.restclienttemplate.TimeFormatter
import org.json.JSONArray
import org.json.JSONObject


class Tweet {

    var TAG : String = "Tweet"

    var body : String = ""
    var createAt : String = ""
    var user : User? = null
    var formattedTime : String = ""

    companion object {
       fun fromJson(jsonObject: JSONObject) : Tweet {
           val tweet = Tweet()
           tweet.body = jsonObject.getString("text")
           tweet.createAt = jsonObject.getString("created_at")
           tweet.user = User.fromJson(jsonObject.getJSONObject("user"))
           tweet.formattedTime = TimeFormatter.getTimeDifference(tweet.createAt);
           Log.i("TAG", "format ${tweet.formattedTime}")
           return tweet
       }
       fun fromJsonArray(jsonArray: JSONArray) : List<Tweet> {
           val tweetList = ArrayList<Tweet>()
           for(i in 0 until jsonArray.length()){
               tweetList.add(fromJson(jsonArray.getJSONObject(i)))
           }
           return tweetList
       }

    }
}