package com.condor.breadtrackermobile

import android.app.Notification
import android.app.Notification.Action
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService()  {

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("Tag", "From: ${message.from}");
        if (message.data.isNotEmpty()) {
            Log.d("Tag", "Message payload: ${message.data}");
            var messageType = message.data.get("bt_message_type")
            if (messageType == "FEED_STARTER_REMINDER") {
                processFeedStarterReminder(message.data);
            }
        }
    }

    private fun processFeedStarterReminder(data: Map<String, String>) {
        var starterUUID = data["starter_uuid"];
        var starterName = data["starter_name"];

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val feedStarterAction = NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            "Feed",
            PendingIntent.getService(applicationContext, 0,
                Intent(
                    ACTION_FEED, Uri.EMPTY, this, FeedStarterService::class.java)
                    .putExtra(STARTER_UUID, starterUUID)
                    .putExtra(STARTER_NAME, starterName),
                PendingIntent.FLAG_IMMUTABLE))
            .build();
        val dismissAction = NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            "Dismiss",
            PendingIntent.getService(applicationContext, 0, Intent(ACTION_DISMISS, Uri.EMPTY, this, FeedStarterService::class.java),
                PendingIntent.FLAG_IMMUTABLE))
            .build();
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Feed sourdough starters")
            .setContentText("It's time to feed $starterName")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .addAction(feedStarterAction)
            .addAction(dismissAction)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        // TODO: Replace 0 with the notification ID
        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}