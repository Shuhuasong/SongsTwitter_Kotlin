package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import android.util.Log
import com.codepath.apps.restclienttemplate.TimeFormatter
import com.codepath.apps.restclienttemplate.TimelineActivity.Companion.TAG
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
class Tweet(var body: String = "", var createAt: String = "", var user: User? = null,  var formattedTime : String = "") :
    Parcelable{

    var TAG : String = "Tweet"

    companion object {
       fun fromJson(jsonObject: JSONObject) : Tweet {
           val tweet = Tweet()
           Log.i(TAG, "${jsonObject.toString()}")
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