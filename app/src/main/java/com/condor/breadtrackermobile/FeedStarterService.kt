package com.condor.breadtrackermobile

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
public const val ACTION_FEED = "com.condor.breadtrackermobile.action.FEED"
public const val ACTION_DISMISS = "com.condor.breadtrackermobile.action.DISMISS"

// TODO: Rename parameters
public const val STARTER_UUID = "com.condor.breadtrackermobile.extra.STARTER_UUID"
public const val STARTER_NAME = "com.condor.breadtrackermobile.extra.STARTER_NAME"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
class FeedStarterService : IntentService("FeedStarterService") {
    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_FEED -> {
                val starterUUID = intent.getStringExtra(STARTER_UUID)
                val starterName = intent.getStringExtra(STARTER_NAME)
                handleActionFeed(starterUUID, starterName)
            }

            ACTION_DISMISS -> {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                // TODO: Replace 0 with the notification ID
                notificationManager.cancel(0)
            }
        }
    }

    /**
     * Handle action Feed in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFeed(starterUUID: String?, starterName: String?) {
        Log.d("Tag", "UUID: $starterUUID Name: $starterName")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // TODO: Replace 0 with the notification ID
        notificationManager.cancel(0)
        // Notify the server
        // TODO: Replace this with a constant or something
        val baseURL = URL("http://192.168.1.68:8080")
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(BreadTrackerAPI::class.java)
        if (!starterUUID.isNullOrEmpty()) {
            GlobalScope.launch {
                val result = api.feedStarter(starterUUID);
                Log.d("Tag", "Call results: ${result.isSuccessful} ${result.code()} ${result.body()}");
            }
        }
        // TODO: Do something with the response here
    }
}