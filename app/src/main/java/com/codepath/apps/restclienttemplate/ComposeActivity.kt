package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose : EditText
    lateinit var btnTweet : Button
    lateinit var client: TwitterClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        client = TwitterApplication.getRestClient(this)

        //etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)

        // Get a reference to the AutoCompleteTextView in the layout
        etCompose = findViewById(R.id.etTweetCompose)

        //AutoComplete Implementation : https://developer.android.com/training/keyboard-input/style#kotlin
        // Get the string array
        val moods: Array<out String> = resources.getStringArray(R.array.mood_array)
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, moods).also { adapter ->
            (etCompose as AutoCompleteTextView).setAdapter(adapter)
        }

        //Handling the user's click on the tweet button
        btnTweet.setOnClickListener{

            //Grab the content in the editText(etCompose)
            val tweetContent = etCompose.text.toString()

            //1. Make sure the tweet isn't empty
            if(tweetContent.isEmpty()){
                Toast.makeText(this, "Content is Empty", Toast.LENGTH_SHORT).show()
            }else{
                if(tweetContent.length > 140){
                    Toast.makeText(this, "Content is too long", Toast.LENGTH_SHORT).show()
                }else
                    //Make an api call to Twitter to publish tweet
                    client.publishTweet(tweetContent, object: JsonHttpResponseHandler(){

                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                            //Send the tweet back to timeline activity without api call
                            Log.i(TAG, "Successfully publish tweet")
                            val tweet = json?.let { it1 -> Tweet.fromJson(it1.jsonObject) }
                            val intent = Intent()
                            intent.putExtra("tweet", tweet)
                            setResult(RESULT_OK, intent)
                            finish()
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                           Log.e(TAG, "Failure on publich tweet")
                        }

                    })
            }
        }
    }

    companion object{
        var TAG = "ComposeAcitivity"
    }
}