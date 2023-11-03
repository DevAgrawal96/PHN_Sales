package com.phntechnolab.sales.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.phntechnolab.sales.R
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.util.Constants.CHANNEL_ID


class NotificationService(private val appContext: Context) {
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotification(channelDescription : String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "action",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = channelDescription


            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showBasicNotification(title : String , content : String) {
        val activityIntent = Intent(appContext, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            appContext,
            1,
            activityIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.phn_technolab_logo)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(activityPendingIntent)
            .build()
        notificationManager.notify(1,notification)
    }
}