package com.nexgen.fitnest.utilis

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nexgen.fitnest.R

fun showNotification2(context: Context) {
    val channelId = "register_notifications"
    val notificationManager = NotificationManagerCompat.from(context)

    // Create the notification channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Register Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for successful registration"
        }
        try {
            notificationManager.createNotificationChannel(channel)
        } catch (e: SecurityException) {
            Log.e("showNotification", "Failed to create notification channel", e)
        }
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.notify_icon)
        .setContentTitle("Registration Successful")
        .setContentText("Your phone number has been registered successfully.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    try {
        notificationManager.notify(1, notification)
    } catch (e: SecurityException) {
        Log.e("showNotification", "Failed to show notification", e)
    }
}