package com.codepath.apps.restclienttemplate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
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

        rvTweet.setDivider(R.drawable.recycler_view_divider)


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
        var REQUEST_CODE = 2022
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        if(item.itemId == R.id.compose){
            val intent = Intent(this, ComposeActivity::class.java)
            editActivityResultLauncher.launch(intent)
            //startActivity(intent)
        }
//        when (item.getItemId()) {
//            R.id.miCompose -> {
//                composeMessage();
//                return true
//            }
//            R.id.miProfile -> {
//                showProfileView()
//                return true
//            }
//            else ->

        return super.onOptionsItemSelected(item)
    }


    var editActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // If the user comes back to this activity from EditActivity
        // with no error or cancellation
        if (result.resultCode == Activity.RESULT_OK) {
            // Get the data passed from EditActivity
            val tweet = result.data?.getParcelableExtra<Tweet>("tweet") as Tweet
            //update timeline
            //modifying the data source of tweets
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0)
            rvTweet.smoothScrollToPosition(0)
        }
    }

    fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
        val divider = DividerItemDecoration(
            this.context,
            DividerItemDecoration.VERTICAL
        )
        val drawable = ContextCompat.getDrawable(
            this.context,
            drawableRes
        )
        drawable?.let {
            divider.setDrawable(it)
            addItemDecoration(divider)
        }
    }
}