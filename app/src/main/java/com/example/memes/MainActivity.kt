package com.example.memes

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MainActivity : AppCompatActivity() {

    var currentImageUrl: String? = null //declaring a global variable which is initially of string nullable type that contains null or no string.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMeme() //calling this function as soon as activity is created
    }

    private fun loadMeme(){ //user created function
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE // we want that progress bar will be visible until image is loaded fully

        //volley library runs at backend thread and calls api and takes care that if same api call is there ..it will fetch the data from cache only and not fetched the same data again using api call
        //val queue = Volley.newRequestQueue(this) -> created a queue using volley which will contain all our http requests made and schedule them accordingly; the activity in which this queue is created is passed here
        val url = "https://meme-api.herokuapp.com/gimme" // url declared from which response will be captured by calling it using api
        var nsfw = false

        //volley library has multiple request options; stringRequest,jsonObjectRequest are one of them
        val jsonObjectRequest = JsonObjectRequest( //creating request
            // GET -> to take data from somewhere; POST-> to send data to backend; PUT-> to update backend data
            Request.Method.GET, url,null, //  Request a jsonRequest response from the provided URL; "null" as we don't want to send anything with the request for now

            { response -> //if request is successful the response comes here
                //Log.d("success Request", response.substring(0,500)) -> kind of print statement that will appear in logcat and prints response that is received from its 0 to 500th letter

                currentImageUrl =
                    response.getString("url") // getting the url from all the response received
                nsfw =
                    response.getBoolean("nsfw") //getting the value of nsfw and if it is true..then that is obscene or inappropriate meme so, we will load another meme
                if (nsfw) {
                    loadMeme()
                }
                else {
                    Glide.with(this).load(currentImageUrl).listener(object :
                        RequestListener<Drawable> { // we have used listener to listen the load request and hides visibility of progress bar when image is ready to be loaded into image view after being downloaded from url; it uses an interface kind of think that is object: RequestListener
                        override fun onLoadFailed( //this executes automatically if load request of image fails
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            findViewById<ProgressBar>(R.id.progressBar).visibility =
                                View.GONE //hiding progress bar if load request fails
                            return false //necessary to return some boolean value, so returning this thing.
                        }
                    }
                            override fun onResourceReady( //this executes automatically when load request  of image is ready to be loaded into imageview
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        findViewById<ProgressBar>(R.id.progressBar).visibility =
                            View.GONE //hiding progress bar if image is ready to be loaded on imageview
                        return false
                    })
                }.into(findViewById(R.id.memeImageView)) //loading the image in imageview using url of the image
             }

            },

            {
                Log.d("error",it.localizedMessage) //it contains current activity and localized message catches the error occurred in that activity and returns the name of exception in the local language of the user(indian,chinese etc)..for this functionality to work the class we are calling this function on (like hello.getLocalizedMessage() so, hello is class whose function is localized one and we are calling it from another class..) should have overwritten this function otherwise it will work same as getMessage() and return the error name only but not in localised or specified form.
            }) // if request is unsuccessful due to some error like internet error, api working error etc the response comes here

        if(!nsfw){ //if nsfw is false then it will execute
            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest) } //Adding the request to the RequestQueue created in MySingleton Class
        //so, volley will then do request handling one by one and take appropriate actions based on request is successful or not

    }

    fun shareMeme(view: View) {
        val intent = Intent(Intent.ACTION_SEND) //it's action is specified as "Intent.ACTION_SEND" so, this intent can help us to send data(specified in putExtra)
        intent.type = "text/plain" // this tells what type of data we want to send so, that it can suggest us apps that can be used to send that data(apps installed on device previously states what all data they can receive and send)
        intent.putExtra(Intent.EXTRA_TEXT, "Hey, Checkout this cool meme I got from Reddit $currentImageUrl")

        val chooser = Intent.createChooser(intent,"Share this meme using...") //creating a chooser to select apps from list that can perform that action and specifying the text that will appear on chooser screen
        startActivity(chooser)
    }

    fun nextMeme(view: View) {
        loadMeme() //calling api again to get new image as response using this function
    }
}