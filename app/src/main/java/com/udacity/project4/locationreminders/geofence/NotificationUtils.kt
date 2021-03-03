package com.udacity.project4.locationreminders.geofence

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.SelectLocationFragment

fun createChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_name),

                NotificationManager.IMPORTANCE_HIGH
        )
                .apply {
                    setShowBadge(false)
                }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = context.getString(R.string.notification_channel_description)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

@SuppressLint("StringFormatInvalid")
fun NotificationManager.sendGeofenceEnteredNotification(context: Context, foundIndex: Int) {
    val contentIntent = Intent(context, SelectLocationFragment::class.java)
    contentIntent.putExtra(GeofencingConstants.EXTRA_GEOFENCE_INDEX, foundIndex)
    val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

//    val mapImage = BitmapFactory.decodeResource(
//            context.resources,
//
//    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.content_text,
                context.getString(GeofencingConstants.LANDMARK_DATA[foundIndex].name)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)

    notify(NOTIFICATION_ID, builder.build())
}

private const val CHANNEL_ID = "GeofenceChannel"
private const val NOTIFICATION_ID = 5