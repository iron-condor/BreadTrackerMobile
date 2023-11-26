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

// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
const val ACTION_FEED = "com.condor.breadtrackermobile.action.FEED"
const val ACTION_DISMISS = "com.condor.breadtrackermobile.action.DISMISS"

const val STARTER_UUID = "com.condor.breadtrackermobile.extra.STARTER_UUID"
const val STARTER_NAME = "com.condor.breadtrackermobile.extra.STARTER_NAME"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class FeedStarterService : IntentService("FeedStarterService") {

    companion object Constants {
        private const val SERVICE_HOST = "192.168.1.68"
        private const val SERVICE_PORT = "8080"
        private const val SERVICE_PROTOCOL = "http"

        const val SERVICE_URL = "$SERVICE_PROTOCOL://$SERVICE_HOST:$SERVICE_PORT"
    }
    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_FEED -> {
                val starterUUID = intent.getStringExtra(STARTER_UUID)
                val starterName = intent.getStringExtra(STARTER_NAME)
                handleActionFeed(starterUUID, starterName)
            }

            ACTION_DISMISS -> {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(STARTER_NOTIF_ID)
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
        notificationManager.cancel(STARTER_NOTIF_ID)
        // Notify the server
        val baseURL = URL(SERVICE_URL)
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