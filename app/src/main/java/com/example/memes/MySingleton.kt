package com.example.memes

import android.content.Context
import android.graphics.Bitmap
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

// If your application makes constant use of the network, it’s probably most efficient to set up a single instance of RequestQueue that will last the lifetime of your app as our app can do one api call at a time and so, if there is only one  request queue for all activities that are calling diff api..we can put them in one queue one by one and can process them accordingly.
//  This ensures that the RequestQueue will last for the lifetime of your app, instead of being recreated every time the activity is recreated (for example, when the user rotates the device).

class MySingleton constructor(context: Context) { // Singleton class have only have one instance.
    companion object {
        @Volatile //INSTANCE is made volatile; if variable is shared btw multiple threads it makes sure that writes or changes to this field are immediately visible to other threads
        private var INSTANCE: MySingleton? = null

        fun getInstance(context: Context) = //instance is being created and if it already exists, it will not be created again
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MySingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy { //we have done it private so, that only MySingleton class can access this or using this class we can access in other classes..so, a single instance will be there
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext) //creating a request queue
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}