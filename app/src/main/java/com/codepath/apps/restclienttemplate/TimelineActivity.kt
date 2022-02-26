package com.codepath.apps.restclienttemplate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    lateinit var swipeContainer : SwipeRefreshLayout

    lateinit var client : TwitterClient
    lateinit var rvTweet : RecyclerView
    lateinit var adapter : TweetAdapter
    val tweets = ArrayList<Tweet>()
    var scrollListener: EndlessRecyclerViewScrollListener? = null
    var linearLayoutManager : LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)

        swipeContainer = findViewById(R.id.swipeContainer)
        
        swipeContainer.setOnRefreshListener {
            populateHomeTimeline()
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );

        rvTweet = findViewById(R.id.rvTweets)
        adapter = TweetAdapter(tweets)
        linearLayoutManager = LinearLayoutManager(this)
        rvTweet.layoutManager = linearLayoutManager
        rvTweet.adapter = adapter

        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager!!) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMoreData()
            }
        }

        //populateHomeTimeline()
    }

    fun loadMoreData(){
        client.getHomeTimeline(object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                Log.i(TAG, "onSuccess")
                val jsonArray = json?.jsonArray
                try {
                    //Clear out our currently fetched tweets
                    adapter.clear()
                    // val newTweetList = Tweet.fromJsonArray(jsonArray)
                    val newTweetList = jsonArray?.let { Tweet.fromJsonArray(it) }
                    if (newTweetList != null) {
                        tweets.addAll(newTweetList)
                    }
                    adapter.notifyDataSetChanged()
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false)
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception $e")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG, "onFailure")
            }

        })
    }


    fun populateHomeTimeline(){
        client.getHomeTimeline(object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                Log.i(TAG, "onSuccess")
                val jsonArray = json?.jsonArray
                try {
                    //Clear out our currently fetched tweets
                    adapter.clear()
                    // val newTweetList = Tweet.fromJsonArray(jsonArray)
                    val newTweetList = jsonArray?.let { Tweet.fromJsonArray(it) }
                    if (newTweetList != null) {
                        tweets.addAll(newTweetList)
                    }
                    adapter.notifyDataSetChanged()
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false)
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception $e")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG, "onFailure")
            }

        })
    }
    companion object{
        var TAG = "TimelineActivity"
    }
}